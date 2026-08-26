package main.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import main.api.request.PasswordRequest;
import main.api.request.RegistrationRequest;
import main.api.request.RestoreRequest;
import main.api.request.UpdateProfileRequest;
import main.api.response.ErrorsResponse;
import main.api.response.ResultResponse;
import main.exception.ValidationException;
import main.mapper.UserMapper;
import main.model.User;
import main.repository.UsersRepository;
import main.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private static final int HASH_LENGTH = 40;
    private static final int REMOVE_PHOTO_OPTION = 1;
    private static final String USER_NOT_FOUND_MESSAGE_PATTERN = "user with email {0} not found";
    private static final String PHOTO_SIZE_ERROR_MESSAGE = "Превышен допустимый размер фотографии (5MB)";
    private static final String PHOTO_ERROR_KEY = "photo";

    private final UsersRepository usersRepository;
    private final UserValidator userValidator;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final MailService mailService;
    private final UserService self;

    @Autowired
    public UserService(UsersRepository usersRepository, UserValidator userValidator, UserMapper mapper, ImageService imageService,
                       PasswordEncoder passwordEncoder, MailService mailService, @Lazy UserService self) {
        this.usersRepository = usersRepository;
        this.userValidator = userValidator;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.imageService = imageService;
        this.self = self;
    }

    @Transactional
    public ErrorsResponse addUser(RegistrationRequest registrationRequest) {
        log.info("Registering new user with email: {}", registrationRequest.email());
        try {
            userValidator.validateRegistration(registrationRequest);
            User user = mapper.fromRegistrationRequestToUser(registrationRequest);
            user.setPassword(passwordEncoder.encode(registrationRequest.password()));
            usersRepository.save(user);
            log.info("User {} registered successfully", registrationRequest.email());
            return new ErrorsResponse(true);
        } catch (ValidationException exception) {
            log.warn("Failed to register user {}: {}", registrationRequest.email(), exception.getErrors());
            return new ErrorsResponse(false, exception.getErrors());
        }
    }

    public User getUserByEmail(String email) {
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format(USER_NOT_FOUND_MESSAGE_PATTERN, email)));
    }

    @Transactional
    public ErrorsResponse updateProfile(UpdateProfileRequest request, MultipartFile photo, String email) {
        log.info("Updating profile for user {}", email);
        User user = getUserByEmail(email);

        if (photo != null) {
            imageService.validateImage(photo, PHOTO_ERROR_KEY, PHOTO_SIZE_ERROR_MESSAGE, null);
            user.setPhoto(imageService.processAndEncodeImage(photo));
        } else if (request.removePhoto() == REMOVE_PHOTO_OPTION) {
            user.setPhoto(null);
        }

        updateBasicUserInfo(request, user);
        log.info("Profile for user {} updated successfully", email);
        return new ErrorsResponse(true);
    }

    public ResultResponse restore(RestoreRequest restoreRequest, HttpServletRequest httpServletRequest) throws MessagingException {
        log.info("Restoring password for user {}", restoreRequest.email());

        Optional<String> hashOptional = self.generateAndSaveRestoreCode(restoreRequest.email());

        if (hashOptional.isPresent()) {
            mailService.sendRestoreEmail(restoreRequest.email(), httpServletRequest.getServerName(), hashOptional.get());
            log.info("Password restoration email sent to {}", restoreRequest.email());
            return new ResultResponse(true);
        }

        log.warn("Failed to restore password for user {}: user not found", restoreRequest.email());
        return new ResultResponse(false);
    }

    @Transactional
    public Optional<String> generateAndSaveRestoreCode(String email) {
        Optional<User> userOptional = usersRepository.findUserByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String hash = RandomUtil.generateRandomHash(HASH_LENGTH);
            user.setCode(hash);
            return Optional.of(hash);
        }
        return Optional.empty();
    }

    @Transactional
    public ErrorsResponse password(PasswordRequest passwordRequest) {
        log.info("Attempting to change password with a restore token");
        User user = userValidator.validatePasswordChange(passwordRequest);
        user.setPassword(passwordEncoder.encode(passwordRequest.password()));
        return new ErrorsResponse(true);
    }

    private void updateBasicUserInfo(UpdateProfileRequest updateProfileRequest, User user) {
        user.setName(updateProfileRequest.name());
        user.setEmail(updateProfileRequest.email());
        if (updateProfileRequest.password() != null) {
            user.setPassword(passwordEncoder.encode(updateProfileRequest.password()));
        }
    }
}
