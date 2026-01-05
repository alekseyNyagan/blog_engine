package main.service;

import main.api.request.GlobalSettingsUpdateRequest;
import main.model.GlobalSetting;
import main.model.enums.GlobalSettingCode;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GlobalSettingsService {
    private final GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public GlobalSettingsService(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public Map<String, Boolean> getGlobalSettings() {
       return globalSettingsRepository.findAll().stream()
                .collect(Collectors.toMap(setting -> setting.getCode().name(), GlobalSetting::getValue));
    }

    @Transactional
    public void updateGlobalSettings(GlobalSettingsUpdateRequest request) {
        Map<GlobalSettingCode, GlobalSetting> settingsMap = globalSettingsRepository.findAll().stream()
                .collect(Collectors.toMap(GlobalSetting::getCode, Function.identity()));

        request.getSettings().forEach((code, value) -> {
            if (settingsMap.containsKey(code)) {
                settingsMap.get(code).setValue(value);
            }
        });

        globalSettingsRepository.saveAll(settingsMap.values());
    }
}
