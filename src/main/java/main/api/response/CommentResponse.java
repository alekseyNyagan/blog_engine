package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response to the client with result of adding a comment")
public record CommentResponse(@Schema(description = "Id of the comment") Integer id) {
}
