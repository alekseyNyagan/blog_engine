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
    private static final byte LIKE_VALUE = 1;
    private static final byte DISLIKE_VALUE = -1;
    private static final String POST_PREMODERATION_SETTING = "POST_PREMODERATION";
    private static final String NON_BREAKING_SPACE = "&nbsp;";
    private static final String HTML_TAG_REGEX = "<.*?>";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    GlobalSettingsService globalSettingsService;

    @Override
    public PostDto toPostDto(Post post) {
        String text = post.getText();
        String textWithoutHtmlTags = text.replaceAll(HTML_TAG_REGEX, "");
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .user(userMapper.toBaseUserDto(post.getUser()))
                .likeCount(getPostVotesCount(post, LIKE_VALUE))
                .dislikeCount(getPostVotesCount(post, DISLIKE_VALUE))
                .commentCount(post.getComments().size())
                .timestamp(getTimestamp(post))
                .announce(textWithoutHtmlTags.length() < ANNOUNCE_LENGTH ? textWithoutHtmlTags
                        : textWithoutHtmlTags.replace(NON_BREAKING_SPACE, " ").substring(0, ANNOUNCE_LENGTH) + "...")
                .build();
    }

    @Override
    public CurrentPostDto toCurrentPostDto(Post post) {
        return CurrentPostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .user(userMapper.toBaseUserDto(post.getUser()))
                .likeCount(getPostVotesCount(post, LIKE_VALUE))
                .dislikeCount(getPostVotesCount(post, DISLIKE_VALUE))
                .comments(post.getComments().stream()
                        .map(postCommentMapper::toPostCommentDto).toList())
                .timestamp(getTimestamp(post))
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
        if (Boolean.TRUE.equals(globalSettingsService.getGlobalSettings().get(POST_PREMODERATION_SETTING)) && currentUser.getIsModerator() != 1) {
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

    private int getPostVotesCount(Post post, byte voteValue) {
        return (int) post.getVotes().stream().filter(vote -> vote.getValue() == voteValue).count();
    }

    private long getTimestamp(Post post) {
        return Timestamp.valueOf(post.getTime()).getTime() / SECOND;
    }
}
