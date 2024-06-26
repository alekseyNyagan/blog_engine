package main.mapper;

import main.dto.UserDTO;
import main.model.enums.ModerationStatus;
import main.model.User;
import main.repository.PostsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class UserMapper extends AbstractMapper<User, UserDTO>{

    private final ModelMapper mapper;
    private final PostsRepository postsRepository;

    @Autowired
    public UserMapper(ModelMapper mapper, PostsRepository postsRepository) {
        super(User.class, UserDTO.class);
        this.mapper = mapper;
        this.postsRepository = postsRepository;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(User.class, UserDTO.class).addMappings(m -> m.skip(UserDTO::setSettings))
                .addMappings(m -> m.skip(UserDTO::setModerationCount)).setPostConverter(toDTOConverter());
    }

    @Override
    void mapSpecificFields(User source, UserDTO destination) {
        boolean isModerator = source.getIsModerator() == 1;
        destination.setSettings(isModerator);
        destination.setModerationCount(isModerator ? postsRepository.countPostsByModerationStatus(ModerationStatus.NEW) : 0);
    }
}
