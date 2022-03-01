package main.error;

public class ImageError extends AbstractError {

    public ImageError(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "image";
    }
}
