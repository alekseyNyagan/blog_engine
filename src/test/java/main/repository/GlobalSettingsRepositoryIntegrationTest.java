package main.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test-containers-flyway")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GlobalSettingsRepositoryIntegrationTest {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @Test
    @DisplayName("Should update global setting")
    void shouldUpdateGlobalSetting() {
        globalSettingsRepository.updateSetting("YES", "MULTIUSER_MODE");
        assertThat(globalSettingsRepository.findById(1).get().getValue()).isEqualTo("YES");
    }
}
