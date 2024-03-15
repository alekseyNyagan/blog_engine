package main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
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

    public GlobalSetting() {
    }

    public GlobalSetting(String code, String name, String value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
