package webserver;

import com.google.common.base.Strings;
import com.google.common.primitives.Booleans;
import db.DataBase;
import model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by User on 2017-08-26.
 */
public class UserHandler {
    public void addUser(String id, String password, String email, String name) {
        DataBase.addUser(new User(id, password, email, name));
    }

    public User getUser(String id) {
        return  DataBase.findUserById(id);
    }

    public boolean canLogin(String id, String password) {
        if(Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(password)){
            return false;
        }
        User user = DataBase.findUserById(id);
        if(user == null){
            return false;
        }
        return password.equals(user.getPassword());
    }

    public boolean isLogined(String isLogined) {
        return Boolean.parseBoolean(isLogined);
    }

    public List<User> getUserList(){
        return DataBase.findAll().stream().collect(Collectors.toList());
    }
}
