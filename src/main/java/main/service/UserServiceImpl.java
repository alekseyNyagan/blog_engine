package main.service;

import main.api.request.*;
import main.api.response.*;
import main.error.*;
import main.mapper.UserMapper;
import main.model.CaptchaCode;
import main.model.User;
import main.repository.CaptchaCodeRepository;
import main.repository.UsersRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private static final int MB = 1_048_576;
    private static final int MAX_PHOTO_SIZE = 5;

    private final UsersRepository usersRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final UserMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository, CaptchaCodeRepository captchaCodeRepository, UserMapper mapper,
                           AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender) {
        this.usersRepository = usersRepository;
        this.captchaCodeRepository = captchaCodeRepository;
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public ErrorsResponse addUser(RegistrationRequest registrationRequest) {
        ErrorsResponse errorsResponse = new ErrorsResponse();
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
            errorsResponse.setResult(true);
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
        errorsResponse.setErrors(errors);
        return errorsResponse;
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
    public ResultResponse logout() {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(true);
        SecurityContextHolder.getContext().setAuthentication(null);
        return resultResponse;
    }

    @Override
    public User getUser() {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        return usersRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user" + email + "not found"));
    }

    @Override
    public ErrorsResponse updateUser(UpdateProfileRequest updateProfileRequest) {
        ErrorsResponse errorsResponse = new ErrorsResponse();
        User user = getUser();
        if (updateProfileRequest.getRemovePhoto() == 1) {
            user.setPhoto(null);
            user.setName(updateProfileRequest.getName());
            user.setEmail(updateProfileRequest.getEmail());
            usersRepository.save(user);
        } else if (!(updateProfileRequest.getPassword() == null)) {
            user.setName(updateProfileRequest.getName());
            user.setEmail(updateProfileRequest.getEmail());
            user.setPassword(passwordEncoder.encode(updateProfileRequest.getPassword()));
            usersRepository.save(user);
        } else {
            user.setName(updateProfileRequest.getName());
            user.setEmail(updateProfileRequest.getEmail());
            usersRepository.save(user);
        }
        errorsResponse.setResult(true);
        return errorsResponse;
    }

    @Override
    public ErrorsResponse updateUserWithPhoto(UpdateProfileRequest updateProfileRequest) throws IOException {
        ErrorsResponse errorsResponse = new ErrorsResponse();
        Map<AbstractError, String> errors = new HashMap<>();
        User user = getUser();
        MultipartFile multipartFile = (MultipartFile) updateProfileRequest.getPhoto();
        if (multipartFile.getSize() <= MAX_PHOTO_SIZE * MB) {
            InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            BufferedImage resizedImage = resizeImage(bufferedImage);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", outputStream);
            String photo = "data:image/png;base64," + StringUtils.newStringUtf8(Base64.encodeBase64(outputStream.toByteArray()));
            if (updateProfileRequest.getPassword() == null) {
                user.setName(updateProfileRequest.getName());
                user.setEmail(updateProfileRequest.getEmail());
                user.setPhoto(photo);
                usersRepository.save(user);
                errorsResponse.setResult(true);
            } else {
                user.setName(updateProfileRequest.getName());
                user.setEmail(updateProfileRequest.getEmail());
                user.setPhoto(photo);
                user.setPassword(passwordEncoder.encode(updateProfileRequest.getPassword()));
                usersRepository.save(user);
                errorsResponse.setResult(true);
            }
        } else {
            PhotoError photoError = new PhotoError("Размер фотографии превышает 5 МБ");
            errors.put(photoError, photoError.getMessage());
        }
        errorsResponse.setErrors(errors);
        return errorsResponse;
    }

    @Override
    public ResultResponse restore(RestoreRequest restoreRequest, HttpServletRequest httpServletRequest) throws MessagingException {
        ResultResponse resultResponse = new ResultResponse();
        Optional<User> user = usersRepository.findUserByEmail(restoreRequest.getEmail());
        if (user.isPresent()) {
            String hash = generateRandomHash(40);
            String domainName = httpServletRequest.getServerName();
            usersRepository.updateUserCode(hash, user.get().getEmail());
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(Message.RecipientType.TO, user.get().getEmail());
            message.setText("<HTML><body> Для восстановления пароля перейдите по ссылке <a href=\"http://" + domainName + "/login/change-password/" + hash + "\">" +
                    "/login/change-password/" + hash + "</a></body></HTML>", "UTF-8", "html");
            javaMailSender.send(message);
            resultResponse.setResult(true);
        }
        return resultResponse;
    }

    @Override
    public ErrorsResponse password(PasswordRequest passwordRequest) {
        ErrorsResponse errorsResponse = new ErrorsResponse();
        Map<AbstractError, String> errors = new HashMap<>();
        Optional<User> user = usersRepository.findUserByCode(passwordRequest.getCode());
        CaptchaCode captchaCode = captchaCodeRepository.findCaptchaCodeBySecretCode(passwordRequest.getCaptchaSecret());
        if (user.isPresent() && captchaCode.getCode().equals(passwordRequest.getCaptcha())) {
            usersRepository.updatePassword(passwordEncoder.encode(passwordRequest.getPassword()), user.get().getId());
            errorsResponse.setResult(true);
        } else {
            if (user.isEmpty()) {
                UserCodeError userCodeError = new UserCodeError("Ссылка для восстановления пароля устарела.\n" +
                        "<a href=\n" +
                        "\\\"/auth/restore\\\">Запросить ссылку снова</a>");
                errors.put(userCodeError, userCodeError.getMessage());
            }
            if (!captchaCode.getCode().equals(passwordRequest.getCaptcha())) {
                CaptchaError captchaError = new CaptchaError();
                errors.put(captchaError, captchaError.getMessage());
            }
        }
        errorsResponse.setErrors(errors);
        return errorsResponse;
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

    private BufferedImage resizeImage(BufferedImage originalImage) throws IOException {
        BufferedImage resizedImage = new BufferedImage(36, 36, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, 36, 36, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private String generateRandomHash(int length) {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
