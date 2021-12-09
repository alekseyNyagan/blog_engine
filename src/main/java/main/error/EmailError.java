package main.error;

public class EmailError extends AbstractError {
    public EmailError() {
        super("Этот e-mail уже зарегистрирован");
    }

    @Override
    public String toString() {
        return  "email";
    }
}
