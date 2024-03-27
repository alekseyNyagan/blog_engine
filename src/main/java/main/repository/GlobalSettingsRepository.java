package main.repository;

import main.model.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSetting, Integer> {

    @Query(nativeQuery = true, value = "UPDATE global_settings SET value = :value WHERE code = :code")
    @Transactional
    @Modifying
    void updateSetting(@Param("value") String value, @Param("code") String code);
}
