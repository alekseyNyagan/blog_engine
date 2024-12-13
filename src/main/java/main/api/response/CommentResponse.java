package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Response to the client with result of adding a comment")
@Data
public class CommentResponse {
    @Schema(description = "Id of the comment")
    private Integer id;
}
