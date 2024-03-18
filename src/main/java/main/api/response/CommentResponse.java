package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.error.AbstractError;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    private Integer id;
    private Boolean result;
    private Map<AbstractError, String> errors;
}
