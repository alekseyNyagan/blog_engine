package main.mapper;

import main.api.request.RegistrationRequest;
import main.dto.BaseUserDto;
import main.dto.PostCommentFlatDto;
import main.dto.UserDto;
import main.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(source = "captchaSecret", target = "code")
    User fromRegistrationRequestToUser(RegistrationRequest registrationRequest);

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "userName", target = "name")
    @Mapping(source = "userPhoto", target = "photo")
    BaseUserDto toBaseUserDto(PostCommentFlatDto postCommentFlatDto);
}