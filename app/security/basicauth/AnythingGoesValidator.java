package security.basicauth;

public class AnythingGoesValidator implements Validator {
    public boolean validate(String username, String password) {
        return true;
    }
}