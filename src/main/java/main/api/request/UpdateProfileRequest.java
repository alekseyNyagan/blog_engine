package main.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileRequest {
    @Schema(description = "Photo of the user")
    private Object photo;
    @Schema(description = "Name of the user")
    private String name;
    @Schema(description = "Email of the user")
    private String email;
    @Schema(description = "Password of the user")
    private String password;
    @Schema(description = "Remove photo of the user. 0 - No, 1 - Yes")
    private int removePhoto;
}
