package authentication.basicauth;

public interface Validator {
    public boolean validate(String username, String password);
}