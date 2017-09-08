package http;

/**
 * Created by User on 2017-09-07.
 */
public class Cookie {
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean ishttpOnly() {
        return ishttpOnly;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public String getPath() {
        return path;
    }

    private String key;
    private String value;
    private boolean ishttpOnly;
    private boolean isSecure;
    private String path;

    public Cookie(String key, String value, boolean ishttpOnly, String path) {
        this.key = key;
        this.value = value;
        this.ishttpOnly = ishttpOnly;
        this.path = path;
    }

}
