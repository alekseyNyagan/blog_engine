package main.service;

import main.api.request.RegistrationRequest;
import main.api.request.UpdateProfileRequest;
import main.api.response.ErrorsResponse;
import main.mapper.UserMapper;
import main.model.User;
import main.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserMapper mapper;
    @Mock
    private ImageService imageService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should add user successfully")
    void testAddUser_Success() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        when(userValidator.validateRegistration(registrationRequest)).thenReturn(Collections.emptyMap());
        when(mapper.fromRegistrationRequestToUser(registrationRequest)).thenReturn(new User());

        ErrorsResponse errorsResponse = userService.addUser(registrationRequest);

        assertTrue(errorsResponse.isResult());
        verify(usersRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should not add user when validation fails")
    void testAddUser_ValidationFails() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        when(userValidator.validateRegistration(registrationRequest)).thenReturn(Map.of("email", "Email already exists"));

        ErrorsResponse errorsResponse = userService.addUser(registrationRequest);

        assertFalse(errorsResponse.isResult());
        assertTrue(errorsResponse.getErrors().containsKey("email"));
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user profile successfully (no photo change)")
    void testUpdateProfile_SuccessNoPhotoChange() throws IOException {
        String email = "test@example.com";
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("New Name");
        User user = new User();

        when(usersRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        ErrorsResponse response = userService.updateProfile(request, null, email);

        assertTrue(response.isResult());
        assertEquals("New Name", user.getName());
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should update user profile and remove photo")
    void testUpdateProfile_RemovePhoto() throws IOException {
        String email = "test@example.com";
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setRemovePhoto(1);
        User user = new User();
        user.setPhoto("path/to/old/photo.jpg");

        when(usersRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        ErrorsResponse response = userService.updateProfile(request, null, email);

        assertTrue(response.isResult());
        assertNull(user.getPhoto());
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should update user profile with new photo")
    void testUpdateProfile_WithNewPhoto() throws IOException {
        String email = "test@example.com";
        UpdateProfileRequest request = new UpdateProfileRequest();
        User user = new User();
        MultipartFile photo = new MockMultipartFile("photo", "new_photo.jpg", "image/jpeg", new byte[10]);

        when(usersRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(imageService.isImageSizeValid(photo)).thenReturn(false);
        when(imageService.processAndEncodeImage(photo)).thenReturn("path/to/new/photo.jpg");

        ErrorsResponse response = userService.updateProfile(request, photo, email);

        assertTrue(response.isResult());
        assertEquals("path/to/new/photo.jpg", user.getPhoto());
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should fail to update profile if photo is too large")
    void testUpdateProfile_PhotoTooLarge() throws IOException {
        String email = "test@example.com";
        UpdateProfileRequest request = new UpdateProfileRequest();
        User user = new User();
        MultipartFile photo = new MockMultipartFile("photo", "large_photo.jpg", "image/jpeg", new byte[10]);

        when(usersRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(imageService.isImageSizeValid(photo)).thenReturn(true);

        ErrorsResponse response = userService.updateProfile(request, photo, email);

        assertFalse(response.isResult());
        assertTrue(response.getErrors().containsKey("photo"));
        verify(usersRepository, never()).save(user);
    }
}
