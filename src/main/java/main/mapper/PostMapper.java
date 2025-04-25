package main.mapper;

import main.api.request.PostRequest;
import main.dto.*;
import main.model.Post;
import main.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@DecoratedWith(PostMapperDelegate.class)
public interface PostMapper {

    @Mapping(target = "timestamp", ignore = true)
    PostDto toPostDto(PostFlatDto post);

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "tags", ignore = true)
    PostDetailsDto toCurrentPostDto(PostDetailsFlatDto postDetailsFlatDto, List<PostCommentFlatDto> postCommentFlatDtos, List<String> tags);

    @Mapping(target = "tags", ignore = true)
    Post fromPostRequestToPost(PostRequest postRequest, User user);

    @Mapping(target = "tags", ignore = true)
    Post fromPostRequestToPost(int id, PostRequest postRequest, User user);
}