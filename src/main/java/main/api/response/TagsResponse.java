package main.api.response;

import main.dto.TagDTO;

import java.util.Objects;
import java.util.Set;

public class TagsResponse {
    private Set<TagDTO> tags;

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagsResponse that = (TagsResponse) o;
        return Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tags);
    }
}
