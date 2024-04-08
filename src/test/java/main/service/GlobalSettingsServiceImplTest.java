package main.service;

import main.api.response.GlobalSettingsResponse;
import main.dto.GlobalSettingDTO;
import main.mapper.GlobalSettingMapper;
import main.model.GlobalSetting;
import main.repository.GlobalSettingsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GlobalSettingsServiceImplTest {

    @Mock
    private GlobalSettingsRepository globalSettingsRepository;

    @Mock
    private GlobalSettingMapper mapper;

    @InjectMocks
    private GlobalSettingsServiceImpl globalSettingsService;

    @Test
    @DisplayName("Should return all global settings")
    public void testGetGlobalSettings() {
        List<GlobalSetting> globalSettings = List.of(
                new GlobalSetting("MULTIUSER_MODE", "YES"),
                new GlobalSetting("POST_PREMODERATION", "NO"),
                new GlobalSetting("STATISTICS_IS_PUBLIC", "YES")
        );

        List<GlobalSettingDTO> globalSettingDTOS = globalSettings.stream()
                .map(gs -> new GlobalSettingDTO(gs.getCode(), "YES".equals(gs.getValue())))
                .toList();

        when(globalSettingsRepository.findAll()).thenReturn(globalSettings);
        when(mapper.toDTO(any(GlobalSetting.class))).thenAnswer(invocation -> {
            GlobalSetting globalSetting = invocation.getArgument(0);
            return globalSettingDTOS.stream()
                    .filter(dto -> dto.getCode().equals(globalSetting.getCode()))
                    .findFirst()
                    .orElse(null);
        });

        GlobalSettingsResponse response = globalSettingsService.getGlobalSettings();

        assertTrue(response.isMultiuserMode());
        assertFalse(response.isPostPremoderation());
        assertTrue(response.isStatisticsIsPublic());
    }
}
