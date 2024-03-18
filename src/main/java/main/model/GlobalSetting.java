package main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    private String value;
}
