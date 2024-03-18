package main.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permission {
    USER("user:write"),
    MODERATE("user:moderate");

    private final String permission;
}
