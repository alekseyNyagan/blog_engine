package main.mapper;

import main.dto.PostCommentDto;
import main.model.PostComment;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class PostCommentMapperDelegate implements PostCommentMapper {

    @Autowired
    UserMapper userMapper;

    @Override
    public PostCommentDto toPostCommentDto(PostComment postComment) {
        return PostCommentDto.builder()
                .id(postComment.getId())
                .text(postComment.getText())
                .user(userMapper.toBaseUserDto(postComment.getUser()))
                .timestamp(postComment.getTime().getEpochSecond())
                .build();
    }
}
