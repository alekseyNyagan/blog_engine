package main.service;

import jakarta.servlet.http.HttpServletResponse;
import main.api.request.*;
import main.api.response.*;
import main.model.User;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface UserService {
    ErrorsResponse addUser(RegistrationRequest registrationRequest);

    LoginResponse login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response);

    LoginResponse check(String email);

    ResultResponse logout();

    User getUser();

    ErrorsResponse updateUser(UpdateProfileRequest updateProfileRequest);

    ErrorsResponse updateUserWithPhoto(UpdateProfileRequest updateProfileRequest) throws IOException;

    ResultResponse restore(RestoreRequest restoreRequest, HttpServletRequest httpServletRequest) throws MessagingException;

    ErrorsResponse password(PasswordRequest passwordRequest);
}
