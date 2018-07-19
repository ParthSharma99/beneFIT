package tech.iosd.benefit.Model;

public class UserFacebookLogin {

    String email;
    String name;
    String fbToken;

    public UserFacebookLogin(String name,String email, String googletoken) {
        this.email = email;
        this.fbToken = googletoken;
        this.name =  name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogletoken() {
        return fbToken;
    }

    public void setGoogletoken(String googletoken) {
        this.fbToken = googletoken;
    }
}
