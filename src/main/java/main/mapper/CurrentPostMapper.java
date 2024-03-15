package main.mapper;

import main.dto.CurrentPostDTO;
import main.model.Post;
import main.model.Tag;
import main.repository.PostsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.sql.Timestamp;
import java.util.stream.Collectors;

@Component
public class CurrentPostMapper extends AbstractMapper<Post, CurrentPostDTO> {

    private static final long SECOND = 1000;
    private final ModelMapper mapper;
    private final PostsRepository postsRepository;
    private final PostCommentMapper postCommentMapper;
    private final IncompleteUserMapper userMapper;

    @Autowired
    public CurrentPostMapper(ModelMapper mapper, PostsRepository postsRepository, PostCommentMapper postCommentMapper,
                      IncompleteUserMapper userMapper) {
        super(Post.class, CurrentPostDTO.class);
        this.mapper = mapper;
        this.postsRepository = postsRepository;
        this.postCommentMapper = postCommentMapper;
        this.userMapper = userMapper;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Post.class, CurrentPostDTO.class)
                .addMappings(m -> m.skip(CurrentPostDTO::setDislikeCount))
                .addMappings(m -> m.skip(CurrentPostDTO::setLikeCount))
                .addMappings(m -> m.skip(CurrentPostDTO::setTimestamp))
                .addMappings(m -> m.skip(CurrentPostDTO::setComments))
                .addMappings(m -> m.skip(CurrentPostDTO::setTags))
                .setPostConverter(toDTOConverter());
    }

    @Override
    public void mapSpecificFields(Post source, CurrentPostDTO destination) {
        Post post = postsRepository.findById(source.getId()).get();
        destination.setUser(userMapper.toDTO(source.getUser()));
        destination.setLikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count());
        destination.setDislikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).count());
        destination.setComments(post.getComments().stream()
                .map(postCommentMapper::toDTO).collect(Collectors.toList()));
        destination.setTimestamp(Timestamp.valueOf(post.getTime()).getTime() / SECOND);
        destination.setTags(post.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
    }
}
