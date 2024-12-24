package main.mapper;

import main.dto.CurrentPostDto;
import main.dto.PostDto;
import main.model.Post;
import org.mapstruct.*;
import org.mapstruct.Mapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@DecoratedWith(PostMapperDelegate.class)
public interface PostMapper {
    PostDto toPostDto(Post post);

    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "tags", ignore = true)
    CurrentPostDto toCurrentPostDto(Post post);
}