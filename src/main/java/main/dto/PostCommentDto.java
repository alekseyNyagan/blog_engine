package main.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "DTO with information about comment")
@EqualsAndHashCode
@Builder
@Data
public class PostCommentDto {
    @Schema(description = "Id of the comment")
    private int id;
    @Schema(description = "Date and time of the comment in UTC format")
    private long timestamp;
    @Schema(description = "Text of the comment")
    private String text;
    private BaseUserDto user;
}
