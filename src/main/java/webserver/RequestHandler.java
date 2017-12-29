package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String requestLine = HttpRequestUtils.getRequestLine(in);
            byte[] body = getBody(requestLine);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
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

    private byte[] getBody(String  requestLine) {
        assert requestLine != null;
        log.debug("Request Line : {}", requestLine);
        String body = "";
        String[] token = requestLine.split(" ");
        if( token.length < 3) {
            throw new RuntimeException();
        }

        String url = token[1];

        try {
            return HttpRequestUtils.readFile(url);
        } catch (IOException e) {
            log.debug("IOException!", e);
            return "안녕 뚝배기".getBytes();
        }
    }

//    private String getHTTPRequestLine(InputStream in) {
//        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            String requestLine = br.readLine();
//            if( requestLine == null || requestLine.contains("HTTP") == false) {
//                new RuntimeException("정상적인 HTTP 요청이 아님");
//            }
//            return requestLine;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
}
