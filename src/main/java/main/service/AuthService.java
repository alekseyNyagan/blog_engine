package main.service;

import lombok.extern.slf4j.Slf4j;
import main.api.request.LoginRequest;
import main.api.response.LoginResponse;
import main.api.response.ResultResponse;
import main.dto.UserDto;
import main.mapper.UserMapper;
import main.model.enums.ModerationStatus;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final UserMapper mapper;

    private final PostsRepository postsRepository;

    public AuthService(AuthenticationManager authenticationManager, UsersRepository usersRepository, UserMapper mapper,
                       PostsRepository postsRepository) {
        this.authenticationManager = authenticationManager;
        this.usersRepository = usersRepository;
        this.mapper = mapper;
        this.postsRepository = postsRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getEmail());
        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.info("User {} successfully logged in", loginRequest.getEmail());
        return getLoginResponse(loginRequest.getEmail());
    }

    public LoginResponse check(String email) {
        return getLoginResponse(email);
    }

    public ResultResponse logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("User {} is logging out", authentication.getName());
        }
        SecurityContextHolder.clearContext();
        return new ResultResponse(true);
    }

    private LoginResponse getLoginResponse(String email) {
        main.model.User authenticatedUser = usersRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        boolean isModerator = authenticatedUser.getIsModerator() == 1;

        UserDto userDto = mapper.toUserDto(authenticatedUser);
        userDto.setModerator(isModerator);
        userDto.setSettings(isModerator);
        userDto.setModerationCount(isModerator ? postsRepository.countPostsByModerationStatus(ModerationStatus.NEW) : 0);
        return new LoginResponse(true, userDto);
    }
}
