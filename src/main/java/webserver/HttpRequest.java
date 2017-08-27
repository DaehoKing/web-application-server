package webserver;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by User on 2017-08-27.
 */
public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private Map<String, String> headerMap = new HashMap<>();
    private Map<String, String> cookieMap = new HashMap<>();
    private Map<String, String> parameterMap = new HashMap<>();
    private HttpMethod method;
    private String path;
    BufferedReader br;

    public HttpRequest(InputStream in) {
        this.br = new BufferedReader(new InputStreamReader(in));
        parseRequestLine();
        parseHeader();
        if(getMethod() == HttpMethod.POST) {
            parseBody();
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String key) {
        return headerMap.get(key);
    }

    public String getParameter(String key) {
        return parameterMap.get(key);
    }

    public String getCookie(String key) {
        return cookieMap.get(key);
    }

    private void parseRequestLine() {
        try {
            String line = br.readLine();
            log.debug("Request Line : {}", line);
            if(Strings.isNullOrEmpty(line)) {
                return;
            }
            String[] tokens = line.split(" ");
            if(tokens.length != 3) {
                return;
            }

            this.method = HttpMethod.valueOf(tokens[0]);
            this.path = tokens[1];
            int qMark = tokens[1].indexOf("?");
            if(qMark != -1) {
                this.path = tokens[1].substring(0, qMark);
                this.parameterMap = parseQueryString(tokens[1].substring(qMark + 1, tokens[1].length()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseHeader() {
        try {
            String header = "";
            while((header = br.readLine()) != null && !"".equals(header) ) {
                log.debug("header : {}", header);
                Pair pair = parseKeyValue(header, ": ");
                if(pair != null) {
                    headerMap.put(pair.getKey(), pair.getValue());
                }
            }
            parseCookie();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseBody() {
        try {
            String contentLength = getHeader("Content-Length");
            if(Strings.isNullOrEmpty(contentLength)) {
                return;
            }
            String body = IOUtils.readData(br, Integer.parseInt(getHeader("Content-Length")));
            if(Strings.isNullOrEmpty(body)) {
                return;
            }
            parameterMap = parseQueryString(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseCookie() {
        String cookie = getHeader("Cookie");
        cookieMap = HttpRequestUtils.parseCookies(cookie);
    }

    private Map<String, String> parseQueryString(String queryString) {
        String[] kvArr = queryString.split("&");
        if(kvArr == null || kvArr.length==0) {
            return Collections.emptyMap();
        }
        return Arrays.stream(kvArr).map(kv -> parseKeyValue(kv, "=")).filter(p->p != null).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private Pair parseKeyValue(String keyValueString, String delim ) {
        if( Strings.isNullOrEmpty(keyValueString) || Strings.isNullOrEmpty(delim)) {
            return null;
        }
        String[] tokens = keyValueString.split(delim);
        return new Pair(tokens[0], tokens[1]);
    }

    private class Pair {
        private String key;
        private String value;

        public Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey(){
            return key;
        }
        public String getValue() {
            return value;
        }
    }
}
