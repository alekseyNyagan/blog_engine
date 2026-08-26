package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Response to the client with errors")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorsResponse(@Schema(description = "Result of the operation. True if the operation was successful or false otherwise") boolean result,
                             @Schema(description = "List of errors of the operation") Map<String, String> errors) {

    public ErrorsResponse(boolean result) {
        this(result, null);
    }
}
