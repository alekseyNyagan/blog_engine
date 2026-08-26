package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response to the client with result of login operation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(@Schema(description = "Result of login operation") boolean result,
                            @Schema(description = "Data with info about logged user") @JsonProperty("user") UserResponse userResponse) {

    public LoginResponse(boolean result) {
        this(result, null);
    }
}
