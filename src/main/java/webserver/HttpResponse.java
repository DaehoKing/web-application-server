package webserver;


import com.google.common.base.Strings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2017-08-27.
 */
public class HttpResponse {

    OutputStream os;
    private int statusCode;
    private String statusMsg;
    private Map<String, String> headerMap;
    private Map<String, String> cookieMap;
    private byte[] responseBody;
    private String resourceDirectory = "./webapp";
    public HttpResponse(OutputStream os) {
        this.os = os;
        this.headerMap = new HashMap<>();
        this.cookieMap = new HashMap<>();
    }

    public void forward(String path) throws IOException {
        statusCode = 200;
        statusMsg = "Ok";
        if(path.contains(".css")) {
            addHeader("Content-Type", "text/css");
        } else {
            addHeader("Content-Type", "text/html;charset=utf-8");
        }
        buildResponseBody(path);
        addHeader("Content-Length", String.valueOf(getResponseBody().length));

        response();
    }

    public void sendRedirect(String path) throws IOException {
        statusCode = 302;
        statusMsg = "Found";
        addHeader("Location", path);

        response();
    }

    public void addHeader(String key, String value) {
        headerMap.put(key, value);
    }

    public String getHeader(String key) {
        return headerMap.get(key);
    }

    public void responseHeader() {
        String output = getStatusLine();

        String cookie = "";
        for(String key : cookieMap.keySet()) {
            String value = cookieMap.get(key);
            if(!Strings.isNullOrEmpty(value)) {
                cookie += key + "=" + value +";";
            }
        }
        if(!Strings.isNullOrEmpty(cookie)) {
            headerMap.put("Set-Cookie", cookie);
        }

        for(String key : headerMap.keySet()) {
            String value = headerMap.get(key);
            if(!Strings.isNullOrEmpty(value)) {
                output += key+ ": " + headerMap.get(key) + "\r\n";
            }
        }

        output += "\r\n";
        try {
            os.write(output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void responseBody() throws IOException {
        try {
            if(responseBody != null) {
                os.write(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            os.close();
        }
    }

    public void response() throws IOException {
        responseHeader();
        responseBody();
    }

    public String getStatusLine() {
        return "HTTP/1.1 " + statusCode + " " + statusMsg + "\r\n";
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    public void buildResponseBody(String path) throws IOException {
        responseBody = Files.readAllBytes(new File(resourceDirectory + path).toPath());
    }

    public void setCookie(String key, String value) {
        cookieMap.put(key, value);
    }
}
