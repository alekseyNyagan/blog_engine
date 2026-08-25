package main.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response to the client with the result of the operation")
public record ResultResponse(@Schema(description = "Result of the operation. True if the operation was successful, false otherwise") boolean result) {
}
