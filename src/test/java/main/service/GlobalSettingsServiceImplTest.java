package main.service;

import main.api.response.GlobalSettingsResponse;
import main.model.GlobalSetting;
import main.repository.GlobalSettingsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@SpringBootTest
public class GlobalSettingsServiceImplTest {

    @Autowired
    private GlobalSettingsServiceImpl globalSettingsService;

    @MockBean
    private GlobalSettingsRepository globalSettingsRepository;

    @BeforeEach
    public void setUp() {
        List<GlobalSetting> settings = List.of(new GlobalSetting("MULTIUSER_MODE", "Многопользовательский режим", "YES"),
                new GlobalSetting("POST_PREMODERATION", "Премодерация постов", "YES"),
                new GlobalSetting("STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "NO"));
        Mockito.when(globalSettingsRepository.findAll()).thenReturn(settings);
    }

    @Test
    public void testGetGlobalSettings() {
        GlobalSettingsResponse expected = new GlobalSettingsResponse();
        expected.setMultiuserMode(true);
        expected.setPostPremoderation(true);
        expected.setStatisticsIsPublic(false);

        GlobalSettingsResponse actual = globalSettingsService.getGlobalSettings();
        Assertions.assertEquals(expected, actual);
    }
}
