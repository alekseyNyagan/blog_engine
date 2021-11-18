package main.service;

import main.dto.TagDTO;

import java.util.Set;

public interface TagsService {
    public Set<TagDTO> getTags();
}
