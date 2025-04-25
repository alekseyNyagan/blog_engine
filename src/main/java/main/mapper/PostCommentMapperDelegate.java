package main.mapper;

import main.dto.BaseUserDto;
import main.dto.PostCommentDto;
import main.dto.PostCommentFlatDto;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class PostCommentMapperDelegate implements PostCommentMapper {

    @Autowired
    UserMapper userMapper;

    @Override
    public PostCommentDto toPostCommentDto(PostCommentFlatDto postCommentFlatDto) {
        return PostCommentDto.builder()
                .id(postCommentFlatDto.id())
                .timestamp(postCommentFlatDto.time().getEpochSecond())
                .text(postCommentFlatDto.text())
                .user(BaseUserDto.builder()
                        .id(postCommentFlatDto.userId())
                        .name(postCommentFlatDto.userName())
                        .photo(postCommentFlatDto.userPhoto())
                        .build())
                .build();
    }
}
