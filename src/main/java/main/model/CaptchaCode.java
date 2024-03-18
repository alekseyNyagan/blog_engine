package main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
}
