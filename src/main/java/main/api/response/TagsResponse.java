package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.dto.TagDTO;

import java.util.Set;

@Schema(description = "Response to the client with the list of existing tags")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagsResponse {
    @Schema(description = "List of tags")
    private Set<TagDTO> tags;
}
