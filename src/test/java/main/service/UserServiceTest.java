package main.service;

import main.api.request.RegistrationRequest;
import main.api.response.ErrorsResponse;
import main.mapper.UserMapper;
import main.model.User;
import main.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private CaptchaCodeService captchaCodeService;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should add user successfully")
    void testAddUser_Success() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setCaptcha("12345");
        registrationRequest.setCaptchaSecret("secret");

        when(captchaCodeService.isCaptchaNotValid(anyString(), anyString())).thenReturn(false);
        when(usersRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());
        when(mapper.fromRegistrationRequestToUser(any(RegistrationRequest.class))).thenReturn(new User());

        ErrorsResponse errorsResponse = userService.addUser(registrationRequest);

        assertTrue(errorsResponse.isResult());
        verify(usersRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should not add user because user with same email already exists")
    void testAddUser_EmailExists() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setCaptcha("12345");
        registrationRequest.setCaptchaSecret("secret");

        when(captchaCodeService.isCaptchaNotValid(anyString(), anyString())).thenReturn(false);

        User existingUser = new User();
        existingUser.setEmail("test@example.com");
        when(usersRepository.findUserByEmail(anyString())).thenReturn(Optional.of(existingUser));

        ErrorsResponse errorsResponse = userService.addUser(registrationRequest);

        assertFalse(errorsResponse.isResult());
        assertTrue(errorsResponse.getErrors().containsKey("email"));
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should not add user because captcha is invalid")
    void testAddUser_InvalidCaptcha() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setCaptcha("wrong-captcha");
        registrationRequest.setCaptchaSecret("secret");

        when(captchaCodeService.isCaptchaNotValid(anyString(), anyString())).thenReturn(true);

        ErrorsResponse errorsResponse = userService.addUser(registrationRequest);

        assertFalse(errorsResponse.isResult());
        assertTrue(errorsResponse.getErrors().containsKey("captcha"));
        verify(usersRepository, never()).save(any(User.class));
    }
}
