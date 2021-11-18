package main.mapper;

import main.dto.PostCommentDTO;
import main.model.PostComment;
import main.repository.PostCommentsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PostCommentMapper extends AbstractMapper<PostComment, PostCommentDTO> {

    private static final int SECOND = 1000;
    private final ModelMapper mapper;
    private final PostCommentsRepository postCommentsRepository;

    @Autowired
    public PostCommentMapper(ModelMapper mapper, PostCommentsRepository postCommentsRepository) {
        super(PostComment.class, PostCommentDTO.class);
        this.mapper = mapper;
        this.postCommentsRepository = postCommentsRepository;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(PostComment.class, PostCommentDTO.class).addMappings(m -> m.skip(PostCommentDTO::setTimestamp))
                .setPostConverter(toDTOConverter());
    }

    @Override
    public void mapSpecificFields(PostComment source, PostCommentDTO destination) {
        destination.setTimestamp(postCommentsRepository.findById(source.getId()).get().getTime().getTime() / SECOND);
    }
}
