package main.service;

import main.api.request.PasswordRequest;
import main.api.request.RegistrationRequest;
import main.exception.ValidationException;
import main.model.User;
import main.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private CaptchaCodeService captchaCodeService;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserValidator userValidator;

    private RegistrationRequest registrationRequest;
    private PasswordRequest passwordRequest;

    @BeforeEach
    void setUp() {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setCaptcha("testCaptcha");
        registrationRequest.setCaptchaSecret("testSecret");
        registrationRequest.setEmail("test@example.com");

        passwordRequest = new PasswordRequest();
        passwordRequest.setCaptcha("testCaptcha");
        passwordRequest.setCaptchaSecret("testSecret");
        passwordRequest.setCode("testCode");
    }

    @Test
    @DisplayName("validateRegistration should throw exception for invalid captcha")
    void testValidateRegistration_InvalidCaptcha() {
        when(captchaCodeService.isCaptchaNotValid("testSecret", "testCaptcha")).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateRegistration(registrationRequest));
        assertTrue(exception.getErrors().containsKey("captcha"));
    }

    @Test
    @DisplayName("validateRegistration should throw exception if email exists")
    void testValidateRegistration_EmailExists() {
        when(captchaCodeService.isCaptchaNotValid("testSecret", "testCaptcha")).thenReturn(false);
        when(usersRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validateRegistration(registrationRequest));
        assertTrue(exception.getErrors().containsKey("email"));
    }

    @Test
    @DisplayName("validateRegistration should not throw exception for valid data")
    void testValidateRegistration_Success() {
        when(captchaCodeService.isCaptchaNotValid("testSecret", "testCaptcha")).thenReturn(false);
        when(usersRepository.findUserByEmail("test@example.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userValidator.validateRegistration(registrationRequest));
    }

    @Test
    @DisplayName("validatePasswordChange should throw exception for invalid captcha")
    void testValidatePasswordChange_InvalidCaptcha() {
        when(captchaCodeService.isCaptchaNotValid("testSecret", "testCaptcha")).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validatePasswordChange(passwordRequest));
        assertTrue(exception.getErrors().containsKey("captcha"));
    }

    @Test
    @DisplayName("validatePasswordChange should throw exception for invalid code")
    void testValidatePasswordChange_InvalidCode() {
        when(captchaCodeService.isCaptchaNotValid("testSecret", "testCaptcha")).thenReturn(false);
        when(usersRepository.findUserByCode("testCode")).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validatePasswordChange(passwordRequest));
        assertTrue(exception.getErrors().containsKey("code"));
    }

    @Test
    @DisplayName("validatePasswordChange should return user for valid data")
    void testValidatePasswordChange_Success() {
        User user = new User();
        when(captchaCodeService.isCaptchaNotValid("testSecret", "testCaptcha")).thenReturn(false);
        when(usersRepository.findUserByCode("testCode")).thenReturn(Optional.of(user));

        User resultUser = userValidator.validatePasswordChange(passwordRequest);
        assertNotNull(resultUser);
        assertEquals(user, resultUser);
    }
}
