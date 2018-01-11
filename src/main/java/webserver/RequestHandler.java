package webserver;

import model.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
            RequestLine requestLine = HttpRequestUtils.getRequestLine(in);
            log.debug("url :"+requestLine.getUrl()+", method : "+requestLine.getMethod());
            byte[] body = getBody(requestLine);
            response200Header(dos, body.length, requestLine.getUrl());
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String url) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+ this.getContentType(url) + "\r\n");
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


    private byte[] getBody(RequestLine  requestLine) {

        try {
            return HttpRequestUtils.readFile(requestLine.getUrl());
        } catch (IOException e) {
            log.debug("IOException!", e);
            return "안녕 뚝배기".getBytes();
        }
    }

    private String getContentType(String url) {
        if(url.contains(".css")) {
            return "text/css";
        } else if(url.contains(".js")) {
            return "application/javascript; charset=UTF-8";
        } else {
            return "text/html;charset=UTF-8";
        }
    }

}
