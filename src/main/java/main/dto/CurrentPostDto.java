package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Schema(description = "DTO with information about post got by id")
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@Data
public final class CurrentPostDto extends BasePostDto {
    @Schema(description = "Open or closed. True for open, false for closed")
    @JsonProperty("active")
    private boolean isActive;
    @Schema(description = "List of comments")
    private List<PostCommentDto> comments;
    @Schema(description = "List of tags")
    private List<String> tags;
    @Schema(description = "Text of post")
    private String text;
}
