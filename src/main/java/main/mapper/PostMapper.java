package main.mapper;

import main.dto.PostDTO;
import main.model.Post;
import main.repository.PostsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.sql.Timestamp;

@Component
public class PostMapper extends AbstractMapper<Post, PostDTO> {

    private static final long SECOND = 1000;
    private static final int ANNOUNCE_LENGTH = 150;
    private final ModelMapper mapper;
    private final PostsRepository postsRepository;
    private final IncompleteUserMapper userMapper;

    @Autowired
    public PostMapper(ModelMapper mapper, PostsRepository postsRepository, IncompleteUserMapper userMapper) {
        super(Post.class, PostDTO.class);
        this.mapper = mapper;
        this.postsRepository = postsRepository;
        this.userMapper = userMapper;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Post.class, PostDTO.class)
                .addMappings(m -> m.skip(PostDTO::setAnnounce)).addMappings(m -> m.skip(PostDTO::setDislikeCount))
                .addMappings(m -> m.skip(PostDTO::setLikeCount)).addMappings(m -> m.skip(PostDTO::setTimestamp))
                .addMappings(m -> m.skip(PostDTO::setCommentCount)).setPostConverter(toDTOConverter());
    }

    @Override
    public void mapSpecificFields(Post source, PostDTO destination) {
        Post post = postsRepository.findById(source.getId()).get();
        String text = post.getText();
        String textWithoutHtmlTags = text.replaceAll("<.*?>", "");
        destination.setUser(userMapper.toDTO(source.getUser()));
        destination.setLikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count());
        destination.setDislikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).count());
        destination.setCommentCount(post.getComments().size());
        destination.setTimestamp(Timestamp.valueOf(post.getTime()).getTime() / SECOND);
        destination.setAnnounce(textWithoutHtmlTags.length() < ANNOUNCE_LENGTH ? textWithoutHtmlTags
                : textWithoutHtmlTags.replace("&nbsp;"," ").substring(0, ANNOUNCE_LENGTH) + "...");
    }
}
