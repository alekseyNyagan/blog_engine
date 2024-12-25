package main.mapper;

import main.api.request.RegistrationRequest;
import main.dto.UserDto;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public abstract class UserMapperDelegate implements UserMapper {

    @Autowired
    private PostsRepository postsRepository;

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
        return new User((byte) 0
                , LocalDateTime.now()
                , registrationRequest.getName()
                , registrationRequest.getEmail()
                , registrationRequest.getPassword()
                , registrationRequest.getCaptchaSecret()
                , null);
    }
}
