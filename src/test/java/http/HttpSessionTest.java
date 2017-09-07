package http;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by User on 2017-09-07.
 */
public class HttpSessionTest {
    @Test
    public void getId() throws FileNotFoundException {

        HttpRequest request2 = new HttpRequest(new FileInputStream("src/test/resources/Http_GET_SESSIONID.txt"));
        HttpSession session2 = request2.getSession();

        String sessionId2= session2.getId();
        assertEquals(sessionId2, "123");
    }
}
