package main.service;

import main.api.request.RegistrationRequest;
import main.error.CaptchaError;
import main.error.EmailError;
import main.error.AbstractError;
import main.model.User;
import main.repository.CaptchaCodeRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final CaptchaCodeRepository captchaCodeRepository;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository, CaptchaCodeRepository captchaCodeRepository) {
        this.usersRepository = usersRepository;
        this.captchaCodeRepository = captchaCodeRepository;
    }

    @Override
    public Map<AbstractError, String> addUser(RegistrationRequest registrationRequest) {
        Map<AbstractError, String> errors = new HashMap<>();
        if (usersRepository.findUserByEmail(registrationRequest.getEmail()).isPresent()) {
            EmailError error = new EmailError();
            errors.put(error, error.getMessage());
        } else  if (!captchaCodeRepository.findCaptchaCodeBySecretCode(registrationRequest.getCaptchaSecret()).getCode().equals(registrationRequest.getCaptcha())) {
            CaptchaError error = new CaptchaError();
            errors.put(error, error.getMessage());
        } else {
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setName(registrationRequest.getName());
            user.setPassword(registrationRequest.getPassword());
            user.setRegTime(new Date());
            usersRepository.save(user);
        }
        return errors;
    }
}
