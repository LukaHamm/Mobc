package app.thecity.model;

import java.io.Serializable;

public class UserInfo implements Serializable {

    public String email;

    public String password;

    public UserInfo(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserInfo(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
