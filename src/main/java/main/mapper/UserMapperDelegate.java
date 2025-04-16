package main.mapper;

import main.api.request.RegistrationRequest;
import main.dto.UserDto;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class UserMapperDelegate implements UserMapper {

    private static final byte COMMON_USER_VALUE = 0;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDto toUserDto(User user) {
        boolean isModerator = user.getIsModerator() == 1;
        return UserDto.builder()
                .moderator(isModerator)
                .settings(isModerator)
                .moderationCount(isModerator ? postsRepository.countPostsByModerationStatus(ModerationStatus.NEW) : 0)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .photo(user.getPhoto())
                .build();
    }

    @Override
    public User fromRegistrationRequestToUser(RegistrationRequest registrationRequest) {
        User user = new User();
        user.setIsModerator(COMMON_USER_VALUE);
        user.setName(registrationRequest.getName());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setCode(registrationRequest.getCaptchaSecret());
        return user;
    }
}
