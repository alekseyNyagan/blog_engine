package main.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileRequest {
    private Object photo;
    private String name;
    private String email;
    private String password;
    private int removePhoto;

    public Object getPhoto() {
        return photo;
    }

    public void setPhoto(Object photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRemovePhoto() {
        return removePhoto;
    }

    public void setRemovePhoto(int removePhoto) {
        this.removePhoto = removePhoto;
    }
}
