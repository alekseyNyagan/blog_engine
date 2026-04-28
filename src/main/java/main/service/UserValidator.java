package main.service;

import main.api.request.PasswordRequest;
import main.api.request.RegistrationRequest;
import main.exception.ValidationException;
import main.model.User;
import main.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserValidator {

    private static final String CAPTCHA_ERROR_MESSAGE = "Код с картинки введён неверно";
    private static final String CAPTCHA_ERROR_KEY = "captcha";
    private static final String USER_WITH_EMAIL_EXISTS_ERROR_MESSAGE = "Пользователь с данным e-mail уже зарегистрирован";
    private static final String USER_WITH_EMAIL_EXISTS_ERROR_KEY = "email";
    private static final String RESTORE_PASSWORD_LINK_EXPIRED_KEY = "code";
    private static final String RESTORE_PASSWORD_LINK_EXPIRED_MESSAGE = """
            Ссылка для восстановления пароля устарела.
            <a href="/auth/restore">Запросить ссылку снова</a>
            """;

    private final CaptchaCodeService captchaCodeService;
    private final UsersRepository usersRepository;

    public UserValidator(CaptchaCodeService captchaCodeService, UsersRepository usersRepository) {
        this.captchaCodeService = captchaCodeService;
        this.usersRepository = usersRepository;
    }

    public void validateRegistration(RegistrationRequest request) {
        Map<String, String> errors = new HashMap<>();
        if (captchaCodeService.isCaptchaNotValid(request.getCaptchaSecret(), request.getCaptcha())) {
            errors.put(CAPTCHA_ERROR_KEY, CAPTCHA_ERROR_MESSAGE);
            throw new ValidationException(errors);
        }
        if (usersRepository.findUserByEmail(request.getEmail()).isPresent()) {
            errors.put(USER_WITH_EMAIL_EXISTS_ERROR_KEY, USER_WITH_EMAIL_EXISTS_ERROR_MESSAGE);
            throw new ValidationException(errors);
        }
    }

    public User validatePasswordChange(PasswordRequest request) {
        Map<String, String> errors = new HashMap<>();
        if (captchaCodeService.isCaptchaNotValid(request.getCaptchaSecret(), request.getCaptcha())) {
            errors.put(CAPTCHA_ERROR_KEY, CAPTCHA_ERROR_MESSAGE);
            throw new ValidationException(errors);
        }

        Optional<User> userOptional = usersRepository.findUserByCode(request.getCode());
        if (userOptional.isEmpty()) {
            errors.put(RESTORE_PASSWORD_LINK_EXPIRED_KEY, RESTORE_PASSWORD_LINK_EXPIRED_MESSAGE);
            throw new ValidationException(errors);
        }
        return userOptional.get();
    }
}
