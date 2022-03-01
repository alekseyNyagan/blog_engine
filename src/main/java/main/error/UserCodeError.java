package main.error;

public class UserCodeError extends AbstractError {

    public UserCodeError(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "code";
    }
}
