package main.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for tag")
public interface TagDTO {
    @Schema(description = "Name of the tag")
    String getName();

    @Schema(description = "Weight of the tag")
    Double getWeight();

    void setName(String name);

    void setWeight(double weight);
}
