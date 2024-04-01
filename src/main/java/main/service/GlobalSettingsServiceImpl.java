package main.service;

import main.api.request.SettingsRequest;
import main.api.response.GlobalSettingsResponse;
import main.dto.GlobalSettingDTO;
import main.mapper.GlobalSettingMapper;
import main.model.GlobalSetting;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GlobalSettingsServiceImpl implements GlobalSettingsService {
    private final GlobalSettingsRepository globalSettingsRepository;
    private final GlobalSettingMapper mapper;

    @Autowired
    public GlobalSettingsServiceImpl(GlobalSettingsRepository globalSettingsRepository, GlobalSettingMapper mapper) {
        this.globalSettingsRepository = globalSettingsRepository;
        this.mapper = mapper;
    }

    @Override
    public GlobalSettingsResponse getGlobalSettings() {
        Map<String, Boolean> settings = globalSettingsRepository.findAll().stream().map(mapper::toDTO)
                .collect(Collectors.toMap(GlobalSettingDTO::getCode, GlobalSettingDTO::isValue));
        return new GlobalSettingsResponse(
                settings.get("MULTIUSER_MODE")
                , settings.get("POST_PREMODERATION")
                , settings.get("STATISTICS_IS_PUBLIC"));
    }

    @Override
    public void updateGlobalSettings(SettingsRequest settingsRequest) {
        Set<GlobalSettingDTO> globalSettingDTOS = Set.of(new GlobalSettingDTO("MULTIUSER_MODE", settingsRequest.isMultiuserMode()),
                new GlobalSettingDTO("POST_PREMODERATION", settingsRequest.isPostPremoderation()),
                new GlobalSettingDTO("STATISTICS_IS_PUBLIC", settingsRequest.isStatisticsIsPublic()));
        Map<String, String> globalSettings = globalSettingDTOS.stream().map(mapper::toEntity).collect(Collectors.toMap(GlobalSetting::getCode, GlobalSetting::getValue));
        globalSettingsRepository.updateSetting(globalSettings.get("MULTIUSER_MODE"), "MULTIUSER_MODE");
        globalSettingsRepository.updateSetting(globalSettings.get("POST_PREMODERATION"), "POST_PREMODERATION");
        globalSettingsRepository.updateSetting(globalSettings.get("STATISTICS_IS_PUBLIC"), "STATISTICS_IS_PUBLIC");
    }
}
