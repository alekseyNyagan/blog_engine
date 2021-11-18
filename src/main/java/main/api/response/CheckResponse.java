package main.api.response;

import main.dto.UserDTO;

public class CheckResponse {
    private boolean result;
    private UserDTO user;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
