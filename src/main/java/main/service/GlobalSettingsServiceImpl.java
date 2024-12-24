package main.service;

import main.dto.GlobalSettingsDto;
import main.model.GlobalSetting;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public GlobalSettingsDto getGlobalSettings() {
        Map<String, Boolean> settings = globalSettingsRepository.findAll().stream()
                .collect(Collectors.toMap(GlobalSetting::getCode, globalSetting -> globalSetting.getValue().equals("YES")
                ));
        return new GlobalSettingsDto(
                settings.get(MULTIUSER_MODE)
                , settings.get(POST_PREMODERATION)
                , settings.get(STATISTICS_IS_PUBLIC));
    }

    @Override
    public void updateGlobalSettings(GlobalSettingsDto globalSettingsDto) {
        Map<String, GlobalSetting> globalSettingsMap = globalSettingsRepository.findAll().stream()
                .collect(Collectors.toMap(GlobalSetting::getCode, globalSetting -> globalSetting));

        globalSettingsMap.get(MULTIUSER_MODE).setValue(globalSettingsDto.isMultiuserMode() ? "YES" : "NO");
        globalSettingsMap.get(POST_PREMODERATION).setValue(globalSettingsDto.isPostPremoderation() ? "YES" : "NO");
        globalSettingsMap.get(STATISTICS_IS_PUBLIC).setValue(globalSettingsDto.isStatisticsIsPublic() ? "YES" : "NO");

        globalSettingsRepository.saveAll(globalSettingsMap.values());
    }
}
