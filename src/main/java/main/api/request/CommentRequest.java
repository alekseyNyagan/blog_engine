package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    @Schema(description = "Comment id to reply")
    @JsonProperty("parent_id")
    private Object parentId;
    @Schema(description = "Post id to comment")
    @JsonProperty("post_id")
    private int postId;
    @Schema(description = "Comment text")
    @NotBlank(message = "Текст комментария пустой")
    @Size(min = 3, message = "Текст комментария слишком короткий")
    private String text;

}
