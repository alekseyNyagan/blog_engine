package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.dto.TagDTO;

import java.util.Set;

@Data
@AllArgsConstructor
public class TagsResponse {
    private Set<TagDTO> tags;
}
