package main.error;

public class CaptchaError extends AbstractError {
    public CaptchaError() {
        super("Код с картинки введён неверно");
    }

    @Override
    public String toString() {
        return "captcha";
    }
}
