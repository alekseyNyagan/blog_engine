package main.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {
    Optional<CaptchaCode> findCaptchaCodeBySecretCode(String secreteCode);

    void deleteAllByTimeLessThan(Instant instant);
}
