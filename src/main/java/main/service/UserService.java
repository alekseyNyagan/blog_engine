package main.service;

import main.api.request.*;
import main.api.response.*;
import main.model.User;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface UserService {
    public ErrorsResponse addUser(RegistrationRequest registrationRequest);

    public LoginResponse login(LoginRequest loginRequest);

    public LoginResponse check(String email);

    public ResultResponse logout();

    public User getUser();

    public ErrorsResponse updateUser(UpdateProfileRequest updateProfileRequest);

    public ErrorsResponse updateUserWithPhoto(UpdateProfileRequest updateProfileRequest) throws IOException;

    public ResultResponse restore(RestoreRequest restoreRequest, HttpServletRequest httpServletRequest) throws MessagingException;

    public ErrorsResponse password(PasswordRequest passwordRequest);
}
