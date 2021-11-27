package main.service;

import main.api.response.TagsResponse;
import main.mapper.TagMapper;
import main.repository.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TagsServiceImpl implements TagsService {
    private final TagMapper mapper;
    private final TagsRepository tagsRepository;

    @Autowired
    public TagsServiceImpl(TagMapper mapper, TagsRepository tagsRepository) {
        this.mapper = mapper;
        this.tagsRepository = tagsRepository;
    }

    @Override
    public TagsResponse getTags() {
        TagsResponse tagResponse = new TagsResponse();
        tagResponse.setTags(tagsRepository.findAll().stream().map(mapper::toDTO).collect(Collectors.toSet()));
        return tagResponse;
    }
}
