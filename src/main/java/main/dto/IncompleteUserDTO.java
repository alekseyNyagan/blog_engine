package main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "DTO with partial user information")
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncompleteUserDTO extends AbstractDTO {
    @Schema(description = "User id")
    private int id;
    @Schema(description = "User name")
    private String name;
    @Schema(description = "User photo")
    private String photo;
}
