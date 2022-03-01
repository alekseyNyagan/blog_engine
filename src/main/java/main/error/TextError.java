package main.error;

public class TextError extends AbstractError {
    public TextError(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "text";
    }
}
