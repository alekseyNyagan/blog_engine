package main.mapper;

import main.api.request.PostRequest;
import main.dto.*;
import main.model.Post;
import org.jsoup.Jsoup;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {InstantMapper.class, PostCommentMapper.class})
public interface PostMapper {

    int ANNOUNCE_LENGTH = 150;

    @Mapping(source = "text", target = "announce", qualifiedByName = "announceOnly")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "userName", target = "user.name")
    PostDto toPostDto(PostFlatDto post);

    @Mapping(source = "postDetailsFlatDto.time", target = "timestamp")
    @Mapping(source = "postCommentFlatDtos", target = "comments")
    @Mapping(source = "postDetailsFlatDto.userId", target = "user.id")
    @Mapping(source = "postDetailsFlatDto.userName", target = "user.name")
    @Mapping(source = "postDetailsFlatDto.userPhoto", target = "user.photo")
    PostDetailsDto toCurrentPostDto(PostDetailsFlatDto postDetailsFlatDto, List<PostCommentFlatDto> postCommentFlatDtos, List<String> tags);

    @Mapping(target = "tags", ignore = true)
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "timestamp", target = "time")
    @Mapping(target = "moderationStatus", expression = "java(main.model.enums.ModerationStatus.ACCEPTED)")
    Post fromPostRequestToPost(PostRequest postRequest);

    @Named("announceOnly")
    default String getAnnounce(String text) {
        String plainText = Jsoup.parse(text).text().replace('\u00A0', ' ').trim();
        return plainText.length() > ANNOUNCE_LENGTH
                ? plainText.substring(0, ANNOUNCE_LENGTH) + "..."
                : plainText;
    }
}