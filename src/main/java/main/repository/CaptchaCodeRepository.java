package main.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {
    public CaptchaCode findCaptchaCodeBySecretCode(String secreteCode);

    @Query(nativeQuery = true, value = "DELETE FROM captcha_codes WHERE time < DATE_SUB(time, INTERVAL :time HOUR)")
    @Modifying
    @Transactional
    public void deleteAllByTimeBefore(@Param("time") int time);
}
