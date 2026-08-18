package main.mapper;

import main.api.response.PostCommentResponse;
import main.dto.PostCommentFlatDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {InstantMapper.class, UserMapper.class})
public interface PostCommentMapper {

    @Mapping(source = "postComment", target = "user")
    @Mapping(source = "time", target = "timestamp")
    PostCommentResponse toPostCommentResponse(PostCommentFlatDto postComment);
}