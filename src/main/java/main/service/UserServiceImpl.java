package main.service;

import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.response.LoginResponse;
import main.api.response.LogoutResponse;
import main.error.CaptchaError;
import main.error.EmailError;
import main.error.AbstractError;
import main.mapper.UserMapper;
import main.model.User;
import main.repository.CaptchaCodeRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final UserMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository, CaptchaCodeRepository captchaCodeRepository, UserMapper mapper,
                           AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.captchaCodeRepository = captchaCodeRepository;
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Map<AbstractError, String> addUser(RegistrationRequest registrationRequest) {
        Map<AbstractError, String> errors = new HashMap<>();
        boolean isEmailExist = usersRepository.findUserByEmail(registrationRequest.getEmail()).isPresent();
        boolean isCaptchaCodeEqual = captchaCodeRepository
                .findCaptchaCodeBySecretCode(registrationRequest.getCaptchaSecret()).getCode().equals(registrationRequest.getCaptcha());
        if (!isEmailExist && isCaptchaCodeEqual) {
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setName(registrationRequest.getName());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRegTime(new Date());
            usersRepository.save(user);
        } else {
            if (!isCaptchaCodeEqual) {
                CaptchaError error = new CaptchaError();
                errors.put(error, error.getMessage());
            }
            if (isEmailExist) {
                EmailError error = new EmailError();
                errors.put(error, error.getMessage());
            }
        }
        return errors;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        return getLoginResponse(user.getUsername());
    }

    @Override
    public LoginResponse check(String email) {
        return getLoginResponse(email);
    }

    @Override
    public LogoutResponse logout() {
        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.setResult(true);
        SecurityContextHolder.getContext().setAuthentication(null);
        return logoutResponse;
    }

    private LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = usersRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserDTO(mapper.toDTO(currentUser));
        return loginResponse;
    }
}
