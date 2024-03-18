package main.api.response;

import lombok.Data;
import main.dto.TagDTO;

import java.util.Set;

@Data
public class TagsResponse {
    private Set<TagDTO> tags;
}
