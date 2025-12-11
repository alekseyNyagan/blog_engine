package main.service;

import main.api.request.LoginRequest;
import main.api.response.LoginResponse;
import main.api.response.ResultResponse;
import main.mapper.UserMapper;
import main.repository.UsersRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final UserMapper mapper;

    public AuthService(AuthenticationManager authenticationManager, UsersRepository usersRepository, UserMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.usersRepository = usersRepository;
        this.mapper = mapper;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return getLoginResponse(loginRequest.getEmail());
    }

    public LoginResponse check(String email) {
        return getLoginResponse(email);
    }

    public ResultResponse logout() {
        SecurityContextHolder.clearContext();
        return new ResultResponse(true);
    }

    private LoginResponse getLoginResponse(String email) {
        main.model.User authenticatedUser = usersRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new LoginResponse(true, mapper.toUserDto(authenticatedUser));
    }
}
