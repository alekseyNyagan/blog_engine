package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends AbstractDTO {
    private int id;
    private String name;
    private String photo;
    private String email;
    @JsonProperty("moderation")
    private boolean moderator;
    private int moderationCount;
    private boolean settings;
}
