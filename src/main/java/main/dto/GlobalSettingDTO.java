package main.dto;

public class GlobalSettingDTO extends AbstractDTO {
    private String code;
    private boolean value;

    public GlobalSettingDTO() {
    }

    public GlobalSettingDTO(String code, boolean value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
