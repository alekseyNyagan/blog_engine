package main.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.api.request.*;
import main.api.response.ErrorsResponse;
import main.api.response.LoginResponse;
import main.api.response.ResultResponse;
import main.mapper.UserMapper;
import main.model.User;
import main.repository.CaptchaCodeRepository;
import main.repository.UsersRepository;
import main.utils.ImageUtil;
import main.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final int MB = 1_048_576;
    private static final int MAX_PHOTO_SIZE = 5;
    private static final int HASH_LENGTH = 40;
    private static final int REMOVE_PHOTO_OPTION = 1;
    private static final String CAPTCHA_ERROR_MESSAGE = "Код с картинки введён неверно";
    private static final String CAPTCHA_ERROR_KEY = "captcha";
    private static final String USER_WITH_EMAIL_EXISTS_ERROR_MESSAGE = "Пользователь с данным e-mail уже зарегистрирован";
    private static final String USER_WITH_EMAIL_EXISTS_ERROR_KEY = "email";
    private static final String USER_NOT_FOUND_MESSAGE_PATTERN = "user with email {0} not found";
    private static final String PHOTO_SIZE_ERROR_MESSAGE = "user with email {0} not found";
    private static final String PHOTO_SIZE_ERROR_KEY = "photo";
    private static final String PHOTO_EXTENSION = "png";
    private static final String PHOTO_PREFIX = "data:image/png;base64,";
    private static final String RESTORE_PASSWORD_TEXT_CHARSET = "UTF-8";
    private static final String RESTORE_PASSWORD_TEXT_SUBTYPE = "html";
    private static final String RESTORE_PASSWORD_LINK_EXPIRED_KEY = "code";
    private static final String RESTORE_PASSWORD_MESSAGE_PATTERN = """
            <HTML><body> Для восстановления пароля перейдите по ссылке <a href="http://{0}/login/change-password/{1}">
            /login/change-password/{2}</a></body></HTML>
            """;
    private static final String RESTORE_PASSWORD_LINK_EXPIRED_MESSAGE = """
            Ссылка для восстановления пароля устарела.
            <a href="/auth/restore">Запросить ссылку снова</a>
            """;
    private final UsersRepository usersRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final UserMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final SecurityContextRepository contextRepository;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository, CaptchaCodeRepository captchaCodeRepository, UserMapper mapper,
                           AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender) {
        this.usersRepository = usersRepository;
        this.captchaCodeRepository = captchaCodeRepository;
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.contextRepository = new HttpSessionSecurityContextRepository();
    }

    @Transactional
    @Override
    public ErrorsResponse addUser(RegistrationRequest registrationRequest) {
        ErrorsResponse errorsResponse = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        boolean isUserWithCurrentEmailExists = usersRepository.findUserByEmail(registrationRequest.getEmail()).isPresent();
        boolean isCaptchaCodeEqual = captchaCodeRepository
                .findCaptchaCodeBySecretCode(registrationRequest.getCaptchaSecret()).getCode().equals(registrationRequest.getCaptcha());
        if (!isCaptchaCodeEqual) {
            errors.put(CAPTCHA_ERROR_KEY, CAPTCHA_ERROR_MESSAGE);
        }
        if (isUserWithCurrentEmailExists) {
            errors.put(USER_WITH_EMAIL_EXISTS_ERROR_KEY, USER_WITH_EMAIL_EXISTS_ERROR_MESSAGE);
        }
        if (errors.isEmpty()) {
            User user = mapper.fromRegistrationRequestToUser(registrationRequest);
            usersRepository.save(user);
            errorsResponse.setResult(true);
        }
        errorsResponse.setErrors(errors);
        return errorsResponse;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        contextRepository.saveContext(context, request, response);
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        return getLoginResponse(user.getUsername());
    }

    @Override
    public LoginResponse check(String email) {
        return getLoginResponse(email);
    }

    @Override
    public ResultResponse logout() {
        SecurityContextHolder.clearContext();
        return new ResultResponse(true);
    }

    @Override
    public User getUser() {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format(USER_NOT_FOUND_MESSAGE_PATTERN, email)));
    }

    @Override
    public ErrorsResponse updateUser(UpdateProfileRequest updateProfileRequest) {
        ErrorsResponse errorsResponse = new ErrorsResponse();
        User user = getUser();
        user.setName(updateProfileRequest.getName());
        user.setEmail(updateProfileRequest.getEmail());
        if (updateProfileRequest.getRemovePhoto() == REMOVE_PHOTO_OPTION) {
            user.setPhoto(null);
        }
        if (updateProfileRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateProfileRequest.getPassword()));
        }
        usersRepository.save(user);
        errorsResponse.setResult(true);
        return errorsResponse;
    }

    @Override
    public ErrorsResponse updateUserWithPhoto(UpdateProfileRequest updateProfileRequest) throws IOException {
        ErrorsResponse errorsResponse = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        User user = getUser();
        MultipartFile multipartFile = (MultipartFile) updateProfileRequest.getPhoto();
        if (multipartFile.getSize() <= MAX_PHOTO_SIZE * MB) {
            user.setName(updateProfileRequest.getName());
            user.setEmail(updateProfileRequest.getEmail());
            user.setPhoto(writePhoto(multipartFile));
            if (updateProfileRequest.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(updateProfileRequest.getPassword()));
            }
            usersRepository.save(user);
            errorsResponse.setResult(true);
        } else {
            errors.put(PHOTO_SIZE_ERROR_KEY, PHOTO_SIZE_ERROR_MESSAGE);
        }
        errorsResponse.setErrors(errors);
        return errorsResponse;
    }

    @Override
    public ResultResponse restore(RestoreRequest restoreRequest, HttpServletRequest httpServletRequest) throws MessagingException {
        ResultResponse resultResponse = new ResultResponse();
        Optional<User> userOptional = usersRepository.findUserByEmail(restoreRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String email = user.getEmail();
            String hash = RandomUtil.generateRandomHash(HASH_LENGTH);
            String domainName = httpServletRequest.getServerName();
            user.setCode(hash);
            usersRepository.save(user);
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(Message.RecipientType.TO, email);
            message.setText(
                    MessageFormat.format(RESTORE_PASSWORD_MESSAGE_PATTERN, domainName, hash, hash),
                    RESTORE_PASSWORD_TEXT_CHARSET,
                    RESTORE_PASSWORD_TEXT_SUBTYPE);
            javaMailSender.send(message);
            resultResponse.setResult(true);
        }
        return resultResponse;
    }

    @Transactional
    @Override
    public ErrorsResponse password(PasswordRequest passwordRequest) {
        ErrorsResponse errorsResponse = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        boolean isCaptchaCodeEqual = captchaCodeRepository.findCaptchaCodeBySecretCode(passwordRequest.getCaptchaSecret())
                .getCode().equals(passwordRequest.getCaptcha());
        if (!isCaptchaCodeEqual) {
            errors.put(CAPTCHA_ERROR_KEY, CAPTCHA_ERROR_MESSAGE);
        }
        Optional<User> userOptional = usersRepository.findUserByCode(passwordRequest.getCode());
        if (userOptional.isEmpty()) {
            errors.put(RESTORE_PASSWORD_LINK_EXPIRED_KEY, RESTORE_PASSWORD_LINK_EXPIRED_MESSAGE);
        }
        if (errors.isEmpty()) {
            userOptional.ifPresent(user -> {
                        user.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
                        errorsResponse.setResult(true);
                    }
            );
        }
        errorsResponse.setErrors(errors);
        return errorsResponse;
    }

    private LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = usersRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new LoginResponse(true, mapper.toUserDto(currentUser));
    }

    private static String writePhoto(MultipartFile multipartFile) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        BufferedImage resizedImage = ImageUtil.resizeImage(bufferedImage, 36, 36);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, PHOTO_EXTENSION, outputStream);
        return PHOTO_PREFIX + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
