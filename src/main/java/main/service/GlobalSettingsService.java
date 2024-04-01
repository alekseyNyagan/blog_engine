package main.service;

import main.api.request.SettingsRequest;
import main.api.response.GlobalSettingsResponse;

public interface GlobalSettingsService {
    GlobalSettingsResponse getGlobalSettings();
    void updateGlobalSettings(SettingsRequest settingsRequest);
}
