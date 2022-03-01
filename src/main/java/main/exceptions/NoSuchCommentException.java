package main.exceptions;

public class NoSuchCommentException extends RuntimeException {
    public NoSuchCommentException(String message) {
        super(message);
    }
}
