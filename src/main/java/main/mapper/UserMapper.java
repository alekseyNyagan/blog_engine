package main.mapper;

import main.api.request.RegistrationRequest;
import main.api.response.BaseUserResponse;
import main.repository.projection.CommentProjection;
import main.api.response.UserResponse;
import main.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface UserMapper {

    @Mapping(source = "isModerator", target = "moderator")
    @Mapping(source = "isModerator", target = "settings")
    UserResponse toUserResponse(User user, boolean isModerator, int moderationCount);

    @Mapping(source = "captchaSecret", target = "code")
    User fromRegistrationRequestToUser(RegistrationRequest registrationRequest);

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "userName", target = "name")
    @Mapping(source = "userPhoto", target = "photo")
    BaseUserResponse toBaseUserResponse(CommentProjection commentProjection);
}