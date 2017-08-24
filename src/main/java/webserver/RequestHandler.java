package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

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

            String path = parseHeader(br);

            byte[] body = getResource(path);

            response200Header(dos, body.length, getContentType(path));
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] readResource(String path) throws IOException {
        return Files.readAllBytes(new File(path).toPath());
    }

    private byte[] getResource(String path) throws IOException {
        if(path == null) {
            return "".getBytes();
        }
        if(path.length() == 0 || path.equals("/")) {
            return readResource("./webapp/index.html");
        }
        return readResource("./webapp" + path);
    }

    private String parseHeader(BufferedReader br) throws IOException {
        String curStr = br.readLine();
        String[] tokens = curStr.split(" ");
        String resourcePath = tokens[1];
        return resourcePath;
    }

    private String getContentType(String path) {
        if(path.indexOf("css") != -1) {
            return "text/css";
        }
        return "text/html;charset=utf-8";
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+contentType+"\r\n");
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
}
