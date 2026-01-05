package main.service;

import main.api.request.GlobalSettingsUpdateRequest;
import main.model.GlobalSetting;
import main.model.enums.GlobalSettingCode;
import main.repository.GlobalSettingsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalSettingsServiceTest {

    @Mock
    private GlobalSettingsRepository globalSettingsRepository;

    @InjectMocks
    private GlobalSettingsService globalSettingsService;

    /**
     * Given the system has global settings stored in the repository<br>
     * When a request is made to retrieve the global settings<br>
     * Then the system should return the current global settings including multiuser mode, post premoderation status, and statistics visibility<br>
     * And the settings should reflect the values stored in the repository
     */
    @Test
    @DisplayName("Should retrieve global settings successfully")
    void testGetGlobalSettings_Success() {
        GlobalSetting multiuserModeSetting = new GlobalSetting(GlobalSettingCode.MULTIUSER_MODE, "Multiuser mode", true);
        GlobalSetting postPremoderationSetting = new GlobalSetting(GlobalSettingCode.POST_PREMODERATION, "Post premoderation", false);
        GlobalSetting statisticsIsPublicSetting = new GlobalSetting(GlobalSettingCode.STATISTICS_IS_PUBLIC, "Statistics is public", true);

        List<GlobalSetting> settingsList = Arrays.asList(multiuserModeSetting, postPremoderationSetting, statisticsIsPublicSetting);
        when(globalSettingsRepository.findAll()).thenReturn(settingsList);

        Map<String, Boolean> response = globalSettingsService.getGlobalSettings();

        assertTrue(response.get("MULTIUSER_MODE"));
        assertFalse(response.get("POST_PREMODERATION"));
        assertTrue(response.get("STATISTICS_IS_PUBLIC"));
    }

    /**
     * Given the system has existing global settings in the repository<br>
     * And a request is made to update the global settings with valid values<br>
     * When the update request is processed<br>
     * Then the system should update the global settings in the repository<br>
     * And the updated settings should reflect the new values for multiuser mode, post premoderation, and statistics visibility
     */
    @Test
    @DisplayName("Should update global settings with valid data")
    void testUpdateGlobalSettings_Success() {
        GlobalSetting multiuserModeSetting = new GlobalSetting(GlobalSettingCode.MULTIUSER_MODE, "Multiuser mode", false);
        GlobalSetting postPremoderationSetting = new GlobalSetting(GlobalSettingCode.POST_PREMODERATION, "Post premoderation", true);
        GlobalSetting statisticsIsPublicSetting = new GlobalSetting(GlobalSettingCode.STATISTICS_IS_PUBLIC, "Statistics is public", false);

        List<GlobalSetting> settingsList = Arrays.asList(multiuserModeSetting, postPremoderationSetting, statisticsIsPublicSetting);
        when(globalSettingsRepository.findAll()).thenReturn(settingsList);

        GlobalSettingsUpdateRequest request = new GlobalSettingsUpdateRequest();
        request.addSetting("MULTIUSER_MODE", true);
        request.addSetting("POST_PREMODERATION", false);
        request.addSetting("STATISTICS_IS_PUBLIC", true);

        globalSettingsService.updateGlobalSettings(request);

        verify(globalSettingsRepository, times(1)).saveAll(any());
        assertTrue(multiuserModeSetting.getValue());
        assertFalse(postPremoderationSetting.getValue());
        assertTrue(statisticsIsPublicSetting.getValue());
    }

    @Test
    @DisplayName("Should throw an exception when updating with an unknown setting")
    void testUpdateGlobalSettings_UnknownSetting() {
        GlobalSettingsUpdateRequest request = new GlobalSettingsUpdateRequest();
        assertThrows(IllegalArgumentException.class, () -> request.addSetting("UNKNOWN_SETTING", true));
    }
}
