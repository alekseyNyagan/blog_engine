package main.mapper;

import main.dto.PostCommentDto;
import main.dto.PostCommentFlatDto;
import main.model.PostComment;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@DecoratedWith(PostCommentMapperDelegate.class)
public interface PostCommentMapper {
    PostComment toEntity(PostCommentDto postCommentDto);

    PostCommentDto toPostCommentDto(PostCommentFlatDto postComment);
}