package main.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import main.api.request.*;
import main.api.response.ErrorsResponse;
import main.api.response.LoginResponse;
import main.api.response.ResultResponse;
import main.mapper.UserMapper;
import main.model.User;
import main.repository.UsersRepository;
import main.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final int HASH_LENGTH = 40;
    private static final int REMOVE_PHOTO_OPTION = 1;
    private static final String CAPTCHA_ERROR_MESSAGE = "Код с картинки введён неверно";
    private static final String CAPTCHA_ERROR_KEY = "captcha";
    private static final String USER_WITH_EMAIL_EXISTS_ERROR_MESSAGE = "Пользователь с данным e-mail уже зарегистрирован";
    private static final String USER_WITH_EMAIL_EXISTS_ERROR_KEY = "email";
    private static final String USER_NOT_FOUND_MESSAGE_PATTERN = "user with email {0} not found";
    private static final String PHOTO_SIZE_ERROR_MESSAGE = "Превышен допустимый размер фотографии (5MB)";
    private static final String PHOTO_SIZE_ERROR_KEY = "photo";
    private static final String RESTORE_PASSWORD_LINK_EXPIRED_KEY = "code";
    private static final String RESTORE_PASSWORD_LINK_EXPIRED_MESSAGE = """
            Ссылка для восстановления пароля устарела.
            <a href="/auth/restore">Запросить ссылку снова</a>
            """;
    private final UsersRepository usersRepository;
    private final CaptchaCodeService captchaCodeService;
    private final UserMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final MailService mailService;

    @Autowired
    public UserService(UsersRepository usersRepository, CaptchaCodeService captchaCodeService, UserMapper mapper, ImageService imageService,
                       AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, MailService mailService) {
        this.usersRepository = usersRepository;
        this.captchaCodeService = captchaCodeService;
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.imageService = imageService;
    }

    @Transactional
    public ErrorsResponse addUser(RegistrationRequest registrationRequest) {
        Map<String, String> errors = new HashMap<>();
        
        if (captchaCodeService.isCaptchaNotValid(registrationRequest.getCaptchaSecret(), registrationRequest.getCaptcha())) {
            errors.put(CAPTCHA_ERROR_KEY, CAPTCHA_ERROR_MESSAGE);
        }
        
        if (usersRepository.findUserByEmail(registrationRequest.getEmail()).isPresent()) {
            errors.put(USER_WITH_EMAIL_EXISTS_ERROR_KEY, USER_WITH_EMAIL_EXISTS_ERROR_MESSAGE);
        }
        
        if (errors.isEmpty()) {
            User user = mapper.fromRegistrationRequestToUser(registrationRequest);
            usersRepository.save(user);
            return new ErrorsResponse(true);
        }
        
        return new ErrorsResponse(false, errors);
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

    public User getUserByEmail(String email) {
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format(USER_NOT_FOUND_MESSAGE_PATTERN, email)));
    }

    @Transactional
    public ErrorsResponse updateUser(UpdateProfileRequest updateProfileRequest, String email) {
        User user = getUserByEmail(email);
        updateBasicUserInfo(updateProfileRequest, user);

        if (updateProfileRequest.getRemovePhoto() == REMOVE_PHOTO_OPTION) {
            user.setPhoto(null);
        }

        usersRepository.save(user);
        return new ErrorsResponse(true);
    }

    @Transactional
    public ErrorsResponse updateUserWithPhoto(UpdateProfileRequest updateProfileRequest, String email) throws IOException {
        Map<String, String> errors = new HashMap<>();
        MultipartFile multipartFile = (MultipartFile) updateProfileRequest.getPhoto();

        if (imageService.isImageSizeValid(multipartFile)) {
            errors.put(PHOTO_SIZE_ERROR_KEY, PHOTO_SIZE_ERROR_MESSAGE);
            return new ErrorsResponse(false, errors);
        }

        User user = getUserByEmail(email);
        updateBasicUserInfo(updateProfileRequest, user);
        user.setPhoto(imageService.processAndEncodeImage(multipartFile));
        usersRepository.save(user);

        return new ErrorsResponse(true);
    }

    @Transactional
    public ResultResponse restore(RestoreRequest restoreRequest, HttpServletRequest httpServletRequest) throws MessagingException {
        Optional<User> userOptional = usersRepository.findUserByEmail(restoreRequest.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String hash = RandomUtil.generateRandomHash(HASH_LENGTH);
            user.setCode(hash);
            usersRepository.save(user);
            mailService.sendRestoreEmail(user.getEmail(), httpServletRequest.getServerName(), hash);
            return new ResultResponse(true);
        }

        return new ResultResponse(false);
    }

    @Transactional
    public ErrorsResponse password(PasswordRequest passwordRequest) {
        Map<String, String> errors = new HashMap<>();

        if (captchaCodeService.isCaptchaNotValid(passwordRequest.getCaptchaSecret(), passwordRequest.getCaptcha())) {
            errors.put(CAPTCHA_ERROR_KEY, CAPTCHA_ERROR_MESSAGE);
        }

        Optional<User> userOptional = usersRepository.findUserByCode(passwordRequest.getCode());
        if (userOptional.isEmpty()) {
            errors.put(RESTORE_PASSWORD_LINK_EXPIRED_KEY, RESTORE_PASSWORD_LINK_EXPIRED_MESSAGE);
        }

        if (errors.isEmpty()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
            return new ErrorsResponse(true);
        }

        return new ErrorsResponse(false, errors);
    }

    private LoginResponse getLoginResponse(String email) {
        main.model.User authenticatedUser = usersRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new LoginResponse(true, mapper.toUserDto(authenticatedUser));
    }

    private void updateBasicUserInfo(UpdateProfileRequest updateProfileRequest, User user) {
        user.setName(updateProfileRequest.getName());
        user.setEmail(updateProfileRequest.getEmail());
        if (updateProfileRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateProfileRequest.getPassword()));
        }
    }
}
