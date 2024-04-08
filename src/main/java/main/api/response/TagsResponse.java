package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.dto.TagDTO;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagsResponse {
    private Set<TagDTO> tags;
}
