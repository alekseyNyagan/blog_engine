package main.service;

import main.api.response.TagsResponse;
import main.repository.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TagsService {
    private final TagsRepository tagsRepository;

    @Autowired
    public TagsService(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    public TagsResponse getTags() {
        return new TagsResponse(tagsRepository.getTags());
    }
}
