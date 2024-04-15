package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import main.dto.PostDTO;

import java.util.List;

@Schema(description = "Response to the client with posts")
@Data
@AllArgsConstructor
public class PostsResponse {
    @Schema(description = "Count of all posts")
    private long count;
    @Schema(description = "Page of posts")
    private List<PostDTO> posts;
}
