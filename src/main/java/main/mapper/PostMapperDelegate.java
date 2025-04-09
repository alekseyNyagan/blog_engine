package main.mapper;

import main.api.request.PostRequest;
import main.dto.CurrentPostDto;
import main.dto.PostDto;
import main.model.Post;
import main.model.Tag;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.service.GlobalSettingsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class PostMapperDelegate implements PostMapper {


    private static final long SECOND = 1000;
    private static final int ANNOUNCE_LENGTH = 150;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    GlobalSettingsService globalSettingsService;

    @Override
    public PostDto toPostDto(Post post) {
        String text = post.getText();
        String textWithoutHtmlTags = text.replaceAll("<.*?>", "");
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .user(userMapper.toBaseUserDto(post.getUser()))
                .likeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count())
                .dislikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).count())
                .commentCount(post.getComments().size())
                .timestamp(Timestamp.valueOf(post.getTime()).getTime() / SECOND)
                .announce(textWithoutHtmlTags.length() < ANNOUNCE_LENGTH ? textWithoutHtmlTags
                        : textWithoutHtmlTags.replace("&nbsp;", " ").substring(0, ANNOUNCE_LENGTH) + "...")
                .build();
    }

    @Override
    public CurrentPostDto toCurrentPostDto(Post post) {
        return CurrentPostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .user(userMapper.toBaseUserDto(post.getUser()))
                .likeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count())
                .dislikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).count())
                .comments(post.getComments().stream()
                        .map(postCommentMapper::toPostCommentDto).toList())
                .timestamp(Timestamp.valueOf(post.getTime()).getTime() / SECOND)
                .tags(post.getTags().stream().map(Tag::getName).toList())
                .isActive(post.getIsActive() == 1)
                .build();
    }

    @Override
    public Post fromPostRequestToPost(PostRequest postRequest, User currentUser) {
        Post post = new Post();
        List<Tag> tags = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        postRequest.getTags().forEach(t -> tags.add(new Tag(t)));
        post.setIsActive(postRequest.getActive());
        post.setTitle(postRequest.getTitle());
        post.setText(postRequest.getText());
        post.setUser(currentUser);
        post.setTags(tags);
        post.setModerationStatus(ModerationStatus.ACCEPTED);
        post.setTime(now);
        if (Boolean.TRUE.equals(globalSettingsService.getGlobalSettings().get("POST_PREMODERATION")) && currentUser.getIsModerator() != 1) {
            post.setModerationStatus(ModerationStatus.NEW);
        }
        if (postRequest.getTimestamp() >= Timestamp.valueOf(now).getTime()) {
            post.setTime(new Timestamp(postRequest.getTimestamp()).toLocalDateTime());
        }
        return post;
    }

    @Override
    public Post fromPostRequestToPost(int id, PostRequest postRequest, User currentUser) {
        Post post = fromPostRequestToPost(postRequest, currentUser);
        post.setId(id);
        return post;
    }
}
