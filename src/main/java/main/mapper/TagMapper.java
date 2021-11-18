package main.mapper;

import main.dto.TagDTO;
import main.model.Tag;
import main.repository.PostsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TagMapper extends AbstractMapper<Tag, TagDTO> {

    private final ModelMapper mapper;
    private final PostsRepository postsRepository;

    @Autowired
    public TagMapper(ModelMapper mapper, PostsRepository postsRepository) {
        super(Tag.class, TagDTO.class);
        this.mapper = mapper;
        this.postsRepository = postsRepository;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Tag.class, TagDTO.class).addMappings(m -> m.skip(TagDTO::setWeight))
                .setPostConverter(toDTOConverter());
    }

    @Override
    void mapSpecificFields(Tag source, TagDTO destination) {
        double postsCount = postsRepository.count();
        double mostPopularTagCount = postsRepository.maxCountPostsByTag();
        double maxWeight = mostPopularTagCount / postsCount;
        double coefficient = 1 / maxWeight;
        double postsCountWithCurrentTag = postsRepository.countPostsByTag(source.getId());
        double tagWeight = postsCountWithCurrentTag / postsCount;
        double weight = coefficient * tagWeight;
        destination.setWeight(weight);
    }
}
