package main.api.response;

import main.dto.TagDTO;

import java.util.Set;

public class TagsResponse {
    private Set<TagDTO> tags;

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }
}
