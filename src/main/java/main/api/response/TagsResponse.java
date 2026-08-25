package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Response to the client with the list of existing tags")
public record TagsResponse(@Schema(description = "List of tags") Set<TagResponse> tags) {
}
