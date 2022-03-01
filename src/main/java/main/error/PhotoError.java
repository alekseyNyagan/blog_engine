package main.error;

public class PhotoError extends AbstractError {

    public PhotoError(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "photo";
    }
}
