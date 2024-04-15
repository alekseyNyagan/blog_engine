package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "Response to the client with result of adding a comment")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    @Schema(description = "Id of the comment")
    private Integer id;
    @Schema(description = "Result of adding a comment. True if the comment was added, false otherwise")
    private Boolean result;
    @Schema(description = "List of errors if the comment was not added")
    private Map<String, String> errors;
}
