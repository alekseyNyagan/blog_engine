package main.error;

public class AbstractError {
    private final String message;

    public AbstractError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
