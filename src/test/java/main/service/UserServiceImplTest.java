package main.service;

import main.api.request.RegistrationRequest;
import main.api.response.ErrorsResponse;
import main.model.CaptchaCode;
import main.model.User;
import main.repository.CaptchaCodeRepository;
import main.repository.UsersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private CaptchaCodeRepository captchaCodeRepository;

    @Test
    public void addUserShouldReturnResultTrue() {
        ErrorsResponse expected = new ErrorsResponse();
        expected.setResult(true);
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("example@mail.ru");
        registrationRequest.setName("Tim");
        registrationRequest.setPassword("111111");
        registrationRequest.setCaptcha("ab4e8");
        registrationRequest.setCaptchaSecret("bcd746a8r3");
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode("ab4e8");
        captchaCode.setSecretCode("bcd746a8r3");
        Mockito.when(usersRepository.findUserByEmail(registrationRequest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(captchaCodeRepository.findCaptchaCodeBySecretCode(registrationRequest.getCaptchaSecret())).thenReturn(captchaCode);

        ErrorsResponse actual = userService.addUser(registrationRequest);

        Assertions.assertEquals(expected.isResult(), actual.isResult());
    }

    @Test
    public void addUserShouldReturnCaptchaError() {
        ErrorsResponse expected = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put("captcha", "Код с картинки введён неверно");
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("example@mail.ru");
        registrationRequest.setName("Tim");
        registrationRequest.setPassword("111111");
        registrationRequest.setCaptcha("ab4e8");
        registrationRequest.setCaptchaSecret("bcd746a8r3");
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode("ab4mn5");
        captchaCode.setSecretCode("bcd746a8r3");
        expected.setErrors(errors);
        Mockito.when(usersRepository.findUserByEmail(registrationRequest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(captchaCodeRepository.findCaptchaCodeBySecretCode(registrationRequest.getCaptchaSecret())).thenReturn(captchaCode);

        ErrorsResponse actual = userService.addUser(registrationRequest);
        Assertions.assertEquals(expected.getErrors(), actual.getErrors());
    }

    @Test
    public void addUserShouldReturnEmailError() {
        ErrorsResponse expected = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Этот e-mail уже зарегистрирован");
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("example@mail.ru");
        registrationRequest.setName("Tim");
        registrationRequest.setPassword("111111");
        registrationRequest.setCaptcha("ab4e8");
        registrationRequest.setCaptchaSecret("bcd746a8r3");
        User user = new User();
        user.setEmail("example@mail.ru");
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode("ab4e8");
        captchaCode.setSecretCode("bcd746a8r3");
        expected.setErrors(errors);
        Mockito.when(usersRepository.findUserByEmail(registrationRequest.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(captchaCodeRepository.findCaptchaCodeBySecretCode(registrationRequest.getCaptchaSecret())).thenReturn(captchaCode);

        ErrorsResponse actual = userService.addUser(registrationRequest);
        Assertions.assertEquals(expected.getErrors(), actual.getErrors());
    }
}
