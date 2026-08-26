package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response to the client with posts")
public record PostsResponse(@Schema(description = "Count of all posts") long count,
                            @Schema(description = "Page of posts") List<PostResponse> posts) {
}
