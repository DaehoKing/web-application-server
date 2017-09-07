package http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by User on 2017-09-07.
 */
public class SessionPool {
    public static Map<String, HttpSession> sessionPool = new HashMap<>();

    public static HttpSession getSession(String sessionId) {
        HttpSession session = sessionPool.get(sessionId);
        if(session == null) {
            session = new HttpSession(sessionId);
            sessionPool.put(sessionId, session);
        }
        return session;
    }
}
