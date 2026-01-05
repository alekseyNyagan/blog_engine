package main.api.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import main.model.enums.GlobalSettingCode;

import java.util.EnumMap;
import java.util.Map;

@Getter
public class GlobalSettingsUpdateRequest {

    @NotNull
    private final Map<GlobalSettingCode, @NotNull Boolean> settings = new EnumMap<>(GlobalSettingCode.class);

    @JsonAnySetter
    public void addSetting(String code, Boolean value) {
        try {
            settings.put(GlobalSettingCode.valueOf(code.toUpperCase()), value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown setting: '" + code + "'");
        }
    }

}
