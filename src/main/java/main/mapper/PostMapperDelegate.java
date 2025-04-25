package main.mapper;

import main.api.request.PostRequest;
import main.dto.*;
import main.model.Post;
import main.model.Tag;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.service.GlobalSettingsService;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class PostMapperDelegate implements PostMapper {

    private static final int ANNOUNCE_LENGTH = 150;
    private static final String POST_PREMODERATION_SETTING = "POST_PREMODERATION";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    GlobalSettingsService globalSettingsService;

    @Override
    public PostDto toPostDto(PostFlatDto post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .user(BaseUserDto.builder().id(post.getUserId()).name(post.getUserName()).build())
                .likeCount(post.getLikeCount())
                .dislikeCount(post.getDislikeCount())
                .commentCount(post.getCommentCount())
                .timestamp(post.getTimestamp().getEpochSecond())
                .announce(getAnnounce(post.getText()))
                .build();
    }

    @Override
    public PostDetailsDto toCurrentPostDto(PostDetailsFlatDto postDetailsFlatDto, List<PostCommentFlatDto> postCommentFlatDtos, List<String> tags) {
        return PostDetailsDto.builder()
                .id(postDetailsFlatDto.id())
                .title(postDetailsFlatDto.title())
                .text(postDetailsFlatDto.text())
                .viewCount(postDetailsFlatDto.viewCount())
                .user(BaseUserDto.builder()
                        .id(postDetailsFlatDto.userId())
                        .name(postDetailsFlatDto.userName())
                        .photo(postDetailsFlatDto.userPhoto())
                        .build())
                .likeCount(postDetailsFlatDto.likeCount())
                .dislikeCount(postDetailsFlatDto.dislikeCount())
                .comments(postCommentFlatDtos.stream()
                        .map(postCommentFlatDto -> postCommentMapper.toPostCommentDto(postCommentFlatDto))
                        .toList())
                .timestamp(postDetailsFlatDto.time().getEpochSecond())
                .tags(tags)
                .active(postDetailsFlatDto.active())
                .build();
    }

    @Override
    public Post fromPostRequestToPost(PostRequest postRequest, User currentUser) {
        Post post = new Post();
        List<Tag> tags = new ArrayList<>();
        postRequest.getTags().forEach(t -> tags.add(new Tag(t)));
        post.setIsActive(postRequest.getActive());
        post.setTitle(postRequest.getTitle());
        post.setText(postRequest.getText());
        post.setUser(currentUser);
        post.setTags(tags);
        post.setModerationStatus(ModerationStatus.ACCEPTED);
        post.setTime(Instant.ofEpochSecond(postRequest.getTimestamp()));
        if (Boolean.TRUE.equals(globalSettingsService.getGlobalSettings().get(POST_PREMODERATION_SETTING)) && currentUser.getIsModerator() != 1) {
            post.setModerationStatus(ModerationStatus.NEW);
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

    private String getAnnounce(String text) {
        String plainText = Jsoup.parse(text).text().replace('\u00A0', ' ').trim();
        return plainText.length() > ANNOUNCE_LENGTH
                ? plainText.substring(0, ANNOUNCE_LENGTH) + "..."
                : plainText;
    }
}
