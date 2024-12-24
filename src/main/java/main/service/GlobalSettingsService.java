package main.service;

import main.dto.GlobalSettingsDto;

public interface GlobalSettingsService {
    GlobalSettingsDto getGlobalSettings();
    void updateGlobalSettings(GlobalSettingsDto globalSettingsDto);
}
