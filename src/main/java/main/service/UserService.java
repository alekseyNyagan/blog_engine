package main.service;

import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.response.LoginResponse;
import main.api.response.LogoutResponse;
import main.error.AbstractError;

import java.util.Map;

public interface UserService {
    public Map<AbstractError, String> addUser(RegistrationRequest registrationRequest);

    public LoginResponse login(LoginRequest loginRequest);

    public LoginResponse check(String email);

    public LogoutResponse logout();
}
