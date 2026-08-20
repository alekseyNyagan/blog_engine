package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for tag")
public interface TagResponse {
    @Schema(description = "Name of the tag")
    String getName();

    @Schema(description = "Weight of the tag")
    Double getWeight();
}
