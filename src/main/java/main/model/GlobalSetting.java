package main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import main.converter.hibernate.BooleanToYesNoConverter;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "global_settings")
public class GlobalSetting extends AbstractEntity {

    @Column(name = "code")
    @NotNull
    private String code;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "value")
    @NotNull
    @Convert(converter = BooleanToYesNoConverter.class)
    private Boolean value;

    public GlobalSetting(String code, Boolean value) {
        this.code = code;
        this.value = value;
    }
}
