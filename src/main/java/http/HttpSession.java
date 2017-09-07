package http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2017-09-07.
 */
public class HttpSession {
    private String id;

    private Map<String, Object> attribute = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }
    public String getId() {
        return this.id;
    }

    public void setAttribute(String name, Object value) {
        this.attribute.put(name, value);
    }

    public Object getAttribute(String name) {
        return this.attribute.get(name);
    }

    public void removeAttribute(String name) {
        this.attribute.remove(name);
    }

    public void invalidate() {
        this.attribute.clear();
    }
}
