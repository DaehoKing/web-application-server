package webserver;

import com.google.common.base.Strings;
import com.google.common.primitives.Booleans;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    private String defualtResource = "/index.html";

    private Map<String, String> headerMap = new HashMap<>();

    private Map<String, String> requestCookie;

    private UserHandler userHandler = new UserHandler();

    private int httpStatus =200;

    private Map<String, String> responseCookie = new HashMap<>();

    private String redirectUrl = "/";

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            byte[] body = "".getBytes();
            if(request.getMethod().isPost()) {
                doPost(request, response);
            } else {
                doGet(request, response);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getRequestUrl() {
        return headerMap.get("requestUrl");
    }

    private String getQueryString() {
        return headerMap.get("queryString");
    }

    private byte[] readResource(String path) throws IOException {
        return Files.readAllBytes(new File(path).toPath());
    }

    private byte[] getResource(String path) throws IOException {
        if(path == null) {
            return "".getBytes();
        }
        if(path.length() == 0 || path.equals("/")) {
            return readResource("./webapp" + defualtResource);
        }

        return readResource("./webapp" + path);
    }

    private void parseHeader(BufferedReader br) throws IOException {
        String header =br.readLine();
        log.debug(header);
        String[] topLine = header.split(" ");
        headerMap.put("method", topLine[0]);
        headerMap.put("requestUrl", topLine[1]);
        int questionMarkPos = topLine[1].indexOf("?");
        if(questionMarkPos != -1) {
            headerMap.put("queryString", topLine[1].substring(questionMarkPos + 1, topLine[1].length()));
            headerMap.put("requestUrl", topLine[1].substring(0 , questionMarkPos));
        }

        while(!"".equals(header = br.readLine())) {
            HttpRequestUtils.Pair pair =  HttpRequestUtils.parseHeader(header);
            if(pair != null) {
                headerMap.put(pair.getKey(), pair.getValue());
            }
        }

        requestCookie = HttpRequestUtils.parseCookies(headerMap.get("Cookie"));
    }


    private String getContentType(String path) {
        if(path.contains("css")) {
            return "text/css";
        }
        return "text/html;charset=utf-8";
    }

    private void response(DataOutputStream dos,  byte[] body) {
        switch (httpStatus) {
            case 200: response200Header(dos, body.length, getContentType(getRequestUrl()));
                break;
            case 302: response302Header(dos);
                break;
            default: response200Header(dos, body.length, getContentType(getRequestUrl()));
        }
        responseBody(dos, body);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+contentType+"\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            setCookie(dos);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void response302Header(DataOutputStream dos){
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            setCookie(dos);
            dos.writeBytes("Location: "+redirectUrl+" \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void setCookie(DataOutputStream dos){
        if(responseCookie.isEmpty()) {
            return;
        }
        try {
            String str = "";
            for (String key: responseCookie.keySet()
                    ) {
                str += key + "=" + responseCookie.get(key) + ";";
            }
            dos.writeBytes("Set-Cookie: "+str+" \r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private void doPost( HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        if(path.contains("/user")){
            if(path.contains("/create")) {
                userHandler.addUser(
                        request.getParameter("userId"),
                        request.getParameter("password"),
                        request.getParameter("email"),
                        request.getParameter("name"));
                response.sendRedirect("/index.html");
            }
            if(path.contains("/login")) {
                if(userHandler.canLogin(
                        request.getParameter("userId"),
                        request.getParameter("password"))) {
                    response.setCookie("logined", "true");
                    response.sendRedirect("/index.html");
                } else {
                    response.setCookie("logined", "false");
                    response.sendRedirect("/login_failed.html");
                }
            }
        }
    }

    private void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();

        if(path.contains("/user/create")) {
            userHandler.addUser(
                    request.getParameter("userId"),
                    request.getParameter("password"),
                    request.getParameter("email"),
                    request.getParameter("name")
            );
            response.sendRedirect("/index.html");
        }
        else if(path.contains("/list")) {
            if(isLogined(request)) {
                buildUserList(userHandler.getUserList()).getBytes();
            }
        }
        else {
            response.forward(path);
        }

    }

    public String buildUserList(List<User> list){
        StringBuilder sb = new StringBuilder("<table>");
        for(User user : list) {
            String row = "";
            row += "<tr>";
            row += "<td>" + user.getUserId() + "</td>";
            row += "<td>" + user.getName() + "</td>";
            row += "<td>" + user.getEmail() + "</td>";
            row += "</tr>";
            sb.append(row);
        }
        sb.append("</table>");
        return sb.toString();
    }

    public boolean isLogined(HttpRequest request) {
        String logined = request.getCookie("logined");
        if(Strings.isNullOrEmpty(logined)) {
            return false;
        }
        return Boolean.parseBoolean(logined);
    }
}
