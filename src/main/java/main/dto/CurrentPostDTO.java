package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "DTO with information about post got by id")
@EqualsAndHashCode(callSuper = true)
@Data
public class CurrentPostDTO extends AbstractDTO {
    @Schema(description = "Id of post")
    private int id;
    @Schema(description = "Date and time of post publication in UTC format")
    private long timestamp;
    @Schema(description = "Open or closed. True for open, false for closed")
    @JsonProperty("active")
    private boolean isActive;
    private IncompleteUserDTO user;
    @Schema(description = "Title of post")
    private String title;
    @Schema(description = "Text of post")
    private String text;
    @Schema(description = "Likes count")
    private int likeCount;
    @Schema(description = "Dislikes count")
    private int dislikeCount;
    @Schema(description = "Views count")
    private int viewCount;
    @Schema(description = "List of comments")
    private List<PostCommentDTO> comments;
    @Schema(description = "List of tags")
    private List<String> tags;
}
