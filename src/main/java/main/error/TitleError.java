package main.error;

public class TitleError extends AbstractError {
    public TitleError(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "title";
    }
}
