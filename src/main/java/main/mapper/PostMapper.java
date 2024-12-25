package main.mapper;

import main.api.request.PostRequest;
import main.dto.CurrentPostDto;
import main.dto.PostDto;
import main.model.Post;
import main.model.User;
import org.mapstruct.*;
import org.mapstruct.Mapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@DecoratedWith(PostMapperDelegate.class)
public interface PostMapper {
    PostDto toPostDto(Post post);

    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "tags", ignore = true)
    CurrentPostDto toCurrentPostDto(Post post);

    @Mapping(target = "tags", ignore = true)
    Post fromPostRequestToPost(PostRequest postRequest, User user);

    @Mapping(target = "tags", ignore = true)
    Post fromPostRequestToPost(int id, PostRequest postRequest, User user);
}