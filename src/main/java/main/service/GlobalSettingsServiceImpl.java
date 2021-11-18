package main.service;

import main.dto.GlobalSettingDTO;
import main.mapper.GlobalSettingMapper;
import main.model.GlobalSetting;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public Map<String, Boolean> getGlobalSettings() {
        List<GlobalSetting> settings = globalSettingsRepository.findAll();
        return settings.stream().map(mapper::toDTO)
                .collect(Collectors.toMap(GlobalSettingDTO::getCode, GlobalSettingDTO::isValue));
    }
}
