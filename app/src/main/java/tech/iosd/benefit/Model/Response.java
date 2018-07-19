package tech.iosd.benefit.Model;

/**
 * Created by SAM33R on 25-05-2018.
 */

public class Response {

    private String message;
    private Boolean success;
    public String mesage;


    public static class Token{
        public String token;
        public int expiresIn;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
        }
    }

    public Token token;


    public String getMessage() {
        return message;
    }

    public Token getToken() {
        return token;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
