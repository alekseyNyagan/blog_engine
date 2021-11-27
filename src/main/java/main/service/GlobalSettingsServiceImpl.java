package main.service;

import main.api.response.GlobalSettingsResponse;
import main.dto.GlobalSettingDTO;
import main.mapper.GlobalSettingMapper;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
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
        GlobalSettingsResponse globalSettingsResponse = new GlobalSettingsResponse();
        Map<String, Boolean> settings = globalSettingsRepository.findAll().stream().map(mapper::toDTO)
                .collect(Collectors.toMap(GlobalSettingDTO::getCode, GlobalSettingDTO::isValue));
        globalSettingsResponse.setMultiuserMode(settings.get("MULTIUSER_MODE"));
        globalSettingsResponse.setPostPremoderation(settings.get("POST_PREMODERATION"));
        globalSettingsResponse.setStatisticsIsPublic(settings.get("STATISTICS_IS_PUBLIC"));
        return globalSettingsResponse;
    }
}
