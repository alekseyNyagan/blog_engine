package main.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateProfileRequest(@Schema(description = "Photo of the user") Object photo,
                                   @Schema(description = "Name of the user") String name,
                                   @Schema(description = "Email of the user") String email,
                                   @Schema(description = "Password of the user") String password,
                                   @Schema(description = "Remove photo of the user. 0 - No, 1 - Yes") int removePhoto) {
}
