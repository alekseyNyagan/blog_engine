package main.service;

import main.api.request.SettingsRequest;
import main.api.response.GlobalSettingsResponse;

public interface GlobalSettingsService {
    public GlobalSettingsResponse getGlobalSettings();
    public void updateGlobalSettings(SettingsRequest settingsRequest);
}
