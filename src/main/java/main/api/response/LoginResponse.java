package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import main.dto.UserDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private boolean result;
    @JsonProperty("user")
    private UserDTO userDTO;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }
}
