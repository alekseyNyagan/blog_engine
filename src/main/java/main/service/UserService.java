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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private static final int HASH_LENGTH = 40;
    private static final int REMOVE_PHOTO_OPTION = 1;
    private static final String USER_NOT_FOUND_MESSAGE_PATTERN = "user with email {0} not found";
    private static final String PHOTO_SIZE_ERROR_MESSAGE = "Превышен допустимый размер фотографии (5MB)";
    private static final String PHOTO_SIZE_ERROR_KEY = "photo";

    private final UsersRepository usersRepository;
    private final UserValidator userValidator;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final MailService mailService;

    @Autowired
    public UserService(UsersRepository usersRepository, UserValidator userValidator, UserMapper mapper, ImageService imageService,
                       PasswordEncoder passwordEncoder, MailService mailService) {
        this.usersRepository = usersRepository;
        this.userValidator = userValidator;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.imageService = imageService;
    }

    @Transactional
    public ErrorsResponse addUser(RegistrationRequest registrationRequest) {
        log.info("Registering new user with email: {}", registrationRequest.getEmail());
        try {
            userValidator.validateRegistration(registrationRequest);
            User user = mapper.fromRegistrationRequestToUser(registrationRequest);
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            usersRepository.save(user);
            log.info("User {} registered successfully", registrationRequest.getEmail());
            return new ErrorsResponse(true);
        } catch (ValidationException exception) {
            log.warn("Failed to register user {}: {}", registrationRequest.getEmail(), exception.getErrors());
            return new ErrorsResponse(false, exception.getErrors());
        }
    }

    public User getUserByEmail(String email) {
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format(USER_NOT_FOUND_MESSAGE_PATTERN, email)));
    }

    @Transactional
    public ErrorsResponse updateProfile(UpdateProfileRequest request, MultipartFile photo, String email) throws IOException {
        log.info("Updating profile for user {}", email);
        User user = getUserByEmail(email);

        if (photo != null) {
            if (imageService.isImageSizeValid(photo)) {
                return new ErrorsResponse(false, Map.of(PHOTO_SIZE_ERROR_KEY, PHOTO_SIZE_ERROR_MESSAGE));
            }
            user.setPhoto(imageService.processAndEncodeImage(photo));
        } else if (request.getRemovePhoto() == REMOVE_PHOTO_OPTION) {
            user.setPhoto(null);
        }

        updateBasicUserInfo(request, user);
        usersRepository.save(user);
        log.info("Profile for user {} updated successfully", email);
        return new ErrorsResponse(true);
    }

    @Transactional
    public ResultResponse restore(RestoreRequest restoreRequest, HttpServletRequest httpServletRequest) throws MessagingException {
        log.info("Restoring password for user {}", restoreRequest.getEmail());
        Optional<User> userOptional = usersRepository.findUserByEmail(restoreRequest.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String hash = RandomUtil.generateRandomHash(HASH_LENGTH);
            user.setCode(hash);
            usersRepository.save(user);
            mailService.sendRestoreEmail(user.getEmail(), httpServletRequest.getServerName(), hash);
            log.info("Password restoration email sent to {}", restoreRequest.getEmail());
            return new ResultResponse(true);
        }
        log.warn("Failed to restore password for user {}: user not found", restoreRequest.getEmail());
        return new ResultResponse(false);
    }

    @Transactional
    public ErrorsResponse password(PasswordRequest passwordRequest) {
        log.info("Attempting to change password with a restore token");
        try {
            User user = userValidator.validatePasswordChange(passwordRequest);
            user.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
            usersRepository.save(user);
            return new ErrorsResponse(true);
        } catch (ValidationException exception) {
            log.warn("Password change failed due to validation errors: {}", exception.getErrors());
            return new ErrorsResponse(false, exception.getErrors());
        }
    }

    private void updateBasicUserInfo(UpdateProfileRequest updateProfileRequest, User user) {
        user.setName(updateProfileRequest.getName());
        user.setEmail(updateProfileRequest.getEmail());
        if (updateProfileRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateProfileRequest.getPassword()));
        }
    }
}
