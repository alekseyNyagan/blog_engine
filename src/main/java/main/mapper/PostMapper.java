package main.mapper;

import main.dto.PostDTO;
import main.model.Post;
import main.model.Tag;
import main.repository.PostCommentsRepository;
import main.repository.PostVotesRepository;
import main.repository.PostsRepository;
import main.repository.TagsRepository;
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
    private final PostVotesRepository postVotesRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final PostCommentMapper postCommentMapper;
    private final UserMapper userMapper;
    private final TagsRepository tagsRepository;

    @Autowired
    public PostMapper(ModelMapper mapper, PostsRepository postsRepository, PostVotesRepository postVotesRepository,
                      PostCommentsRepository postCommentsRepository, PostCommentMapper postCommentMapper,
                      UserMapper userMapper, TagsRepository tagsRepository) {
        super(Post.class, PostDTO.class);
        this.mapper = mapper;
        this.postsRepository = postsRepository;
        this.postVotesRepository = postVotesRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.postCommentMapper = postCommentMapper;
        this.userMapper = userMapper;
        this.tagsRepository = tagsRepository;
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
        String text = postsRepository.findById(source.getId()).get().getText();
        String regex = "\\<.*?\\>";
        destination.setUser(userMapper.toDTO(source.getUser()));
        destination.setLikeCount(postVotesRepository.countPostVoteByPostAndValue(source, (byte) 1));
        destination.setDislikeCount(postVotesRepository.countPostVoteByPostAndValue(source, (byte) -1));
        destination.setCommentCount(postCommentsRepository.countPostCommentsByPost(source));
        destination.setComments(postCommentsRepository.findPostCommentsByPost(source).stream()
                .map(postCommentMapper::toDTO).collect(Collectors.toList()));
        destination.setTimestamp(postsRepository.findById(source.getId()).get().getTime().getTime() / SECOND);
        destination.setAnnounce(text.length() < ANNOUNCE_LENGTH ? text.replaceAll(regex, "")
                : text.substring(0, ANNOUNCE_LENGTH).replaceAll(regex, "") + "...");
        destination.setTags(tagsRepository.findTagsByPostId(source.getId()).stream().map(Tag::getName).collect(Collectors.toList()));
    }
}
