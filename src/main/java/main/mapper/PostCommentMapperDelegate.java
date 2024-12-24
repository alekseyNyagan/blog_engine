package main.mapper;

import main.dto.PostCommentDto;
import main.model.PostComment;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

public abstract class PostCommentMapperDelegate implements PostCommentMapper {

    private static final int SECOND = 1000;

    @Autowired
    UserMapper userMapper;

    @Override
    public PostCommentDto toPostCommentDto(PostComment postComment) {
        return PostCommentDto.builder()
                .id(postComment.getId())
                .text(postComment.getText())
                .user(userMapper.toBaseUserDto(postComment.getUser()))
                .timestamp(Timestamp.valueOf(postComment.getTime()).getTime() / SECOND)
                .build();
    }
}
