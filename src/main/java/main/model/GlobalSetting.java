package main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import main.converter.hibernate.BooleanToYesNoConverter;
import main.model.enums.GlobalSettingCode;

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
    @Enumerated(EnumType.STRING)
    private GlobalSettingCode code;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "value")
    @NotNull
    @Convert(converter = BooleanToYesNoConverter.class)
    private Boolean value;
}
