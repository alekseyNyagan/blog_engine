package main.service;

import main.model.GlobalSetting;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GlobalSettingsServiceImpl implements GlobalSettingsService {
    private final GlobalSettingsRepository globalSettingsRepository;

    private static final String MULTIUSER_MODE = "MULTIUSER_MODE";
    private static final String POST_PREMODERATION = "POST_PREMODERATION";
    private static final String STATISTICS_IS_PUBLIC = "STATISTICS_IS_PUBLIC";

    @Autowired
    public GlobalSettingsServiceImpl(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    @Override
    public Map<String, Boolean> getGlobalSettings() {
       return globalSettingsRepository.findAll().stream()
                .collect(Collectors.toMap(GlobalSetting::getCode, GlobalSetting::getValue));
    }

    @Override
    @Transactional
    public void updateGlobalSettings(Map<String, Boolean> globalSettings) {
        Map<String, GlobalSetting> globalSettingsMap = globalSettingsRepository.findAll().stream()
                .collect(Collectors.toMap(GlobalSetting::getCode, globalSetting -> globalSetting));

        globalSettingsMap.get(MULTIUSER_MODE).setValue(globalSettings.get(MULTIUSER_MODE));
        globalSettingsMap.get(POST_PREMODERATION).setValue(globalSettings.get(POST_PREMODERATION));
        globalSettingsMap.get(STATISTICS_IS_PUBLIC).setValue(globalSettings.get(STATISTICS_IS_PUBLIC));

        globalSettingsRepository.saveAll(globalSettingsMap.values());
    }
}
