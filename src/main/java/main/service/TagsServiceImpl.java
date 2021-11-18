package main.service;

import main.dto.TagDTO;
import main.mapper.TagMapper;
import main.model.Tag;
import main.repository.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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
    public Set<TagDTO> getTags() {
        List<Tag> allTags = tagsRepository.findAll();
        return allTags.stream().map(mapper::toDTO).collect(Collectors.toSet());
    }
}
