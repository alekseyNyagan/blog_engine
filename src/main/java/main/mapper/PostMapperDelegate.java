package main.mapper;

import main.dto.CurrentPostDto;
import main.dto.PostDto;
import main.model.Post;
import main.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

public abstract class PostMapperDelegate implements PostMapper {


    private static final long SECOND = 1000;
    private static final int ANNOUNCE_LENGTH = 150;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostCommentMapper postCommentMapper;

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
}
