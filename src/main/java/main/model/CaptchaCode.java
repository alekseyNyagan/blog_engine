package main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCode extends AbstractEntity {

    @Column(name = "time")
    @NotNull
    private LocalDateTime time;

    @Column(name = "code")
    @NotNull
    private String code;

    @Column(name = "secret_code")
    @NotNull
    private String secretCode;

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}
