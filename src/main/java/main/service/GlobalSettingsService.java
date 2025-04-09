package main.service;

import java.util.Map;

public interface GlobalSettingsService {
    Map<String, Boolean> getGlobalSettings();
    void updateGlobalSettings(Map<String, Boolean> globalSettings);
}
