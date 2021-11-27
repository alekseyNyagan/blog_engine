package main.mapper;

import main.dto.PostDTO;
import main.model.Post;
import main.model.Tag;
import main.repository.PostsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Component
public class PostMapper extends AbstractMapper<Post, PostDTO> {

    private static final long SECOND = 1000;
    private static final int ANNOUNCE_LENGTH = 150;
    private final ModelMapper mapper;
    private final PostsRepository postsRepository;
    private final PostCommentMapper postCommentMapper;
    private final UserMapper userMapper;

    @Autowired
    public PostMapper(ModelMapper mapper, PostsRepository postsRepository, PostCommentMapper postCommentMapper,
                      UserMapper userMapper) {
        super(Post.class, PostDTO.class);
        this.mapper = mapper;
        this.postsRepository = postsRepository;
        this.postCommentMapper = postCommentMapper;
        this.userMapper = userMapper;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Post.class, PostDTO.class)
                .addMappings(m -> m.skip(PostDTO::setAnnounce)).addMappings(m -> m.skip(PostDTO::setDislikeCount))
                .addMappings(m -> m.skip(PostDTO::setLikeCount)).addMappings(m -> m.skip(PostDTO::setTimestamp))
                .addMappings(m -> m.skip(PostDTO::setComments)).addMappings(m -> m.skip(PostDTO::setCommentCount))
                .addMappings(m -> m.skip(PostDTO::setTags)).setPostConverter(toDTOConverter());
    }

    @Override
    public void mapSpecificFields(Post source, PostDTO destination) {
        Post post = postsRepository.findById(source.getId()).get();
        String text = post.getText();
        String htmlTagRegex = "\\<.*?\\>";
        destination.setUser(userMapper.toDTO(source.getUser()));
        destination.setLikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count());
        destination.setDislikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).count());
        destination.setCommentCount(post.getComments().size());
        destination.setComments(post.getComments().stream()
                .map(postCommentMapper::toDTO).collect(Collectors.toList()));
        destination.setTimestamp(post.getTime().getTime() / SECOND);
        destination.setAnnounce(text.length() < ANNOUNCE_LENGTH ? text.replaceAll(htmlTagRegex, "")
                : text.substring(0, ANNOUNCE_LENGTH).replaceAll(htmlTagRegex, "") + "...");
        destination.setTags(post.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
    }
}
