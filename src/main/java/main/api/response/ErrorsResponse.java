package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "Response to the client with errors")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorsResponse {
    @Schema(description = "Result of the operation. True if the operation was successful or false otherwise")
    private boolean result;
    @Schema(description = "List of errors of the operation")
    private Map<String, String> errors;
}
