package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response to the client with the result of the operation")
public class ResultResponse {
    @Schema(description = "Result of the operation. True if the operation was successful, false otherwise")
    private boolean result;
}
