import com.sun.org.apache.regexp.internal.RE;
import model.User;
import org.junit.Test;
import webserver.RequestHandler;
import webserver.UserHandler;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by User on 2017-08-26.
 */
public class RequestHandlerTest {
    RequestHandler handler = new RequestHandler(null);
    @Test
    public void  회원가입__테스트() {
        String path = "http://localhost:8080/user/create?userId=a&password=1234&email=c&name=d";

    }
    @Test
    public void test(){
        UserHandler handler = new UserHandler();
        handler.getUserList();
    }
}
