package main.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RestoreRequest {
    @Schema(description = "Email of user to send restore password info")
    private String email;
}
