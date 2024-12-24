package main.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Schema(description = "DTO with post data")
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@Data
public final class PostDto extends BasePostDto {
    @Schema(description = "Small part of post content")
    private String announce;
    @Schema(description = "Comments count")
    private int commentCount;
}
