package main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Schema(description = "DTO with partial user information")
@EqualsAndHashCode
@ToString
@SuperBuilder(toBuilder = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseUserDto {
    @Schema(description = "User id")
    private int id;
    @Schema(description = "User name")
    private String name;
    @Schema(description = "User photo")
    private String photo;
}
