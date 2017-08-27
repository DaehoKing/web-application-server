package webserver;

/**
 * Created by User on 2017-08-28.
 */
public enum HttpMethod {
    POST,
    GET;

    public boolean isPost() {
        return this == POST;
    }

}
