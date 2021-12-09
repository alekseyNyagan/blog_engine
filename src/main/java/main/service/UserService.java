package main.service;

import main.api.request.RegistrationRequest;
import main.error.AbstractError;

import java.util.Map;

public interface UserService {
    public Map<AbstractError, String> addUser(RegistrationRequest registrationRequest);
}
