package http;

import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by User on 2017-09-07.
 */
public class HttpCookies {
    private Map<String, String> cookies;

    public HttpCookies(String cookies) {
        this.cookies = HttpRequestUtils.parseCookies(cookies);
    }
    public String getCookie(String key) {
        return cookies.get(key);
    }

    public void setCookie(String key, String value) {
        cookies.put(key, value);
    }

}
