package main.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileRequest {
    private Object photo;
    private String name;
    private String email;
    private String password;
    private int removePhoto;
}
