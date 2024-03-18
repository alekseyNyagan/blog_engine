package main.model;

import lombok.*;
import main.model.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User extends AbstractEntity {

    @Column(name = "is_moderator")
    @NotNull
    private byte isModerator;

    @Column(name = "reg_time")
    @NotNull
    private LocalDateTime regTime;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "email")
    @NotNull
    private String email;

    @Column(name = "password")
    @NotNull
    private String password;

    @Column(name = "code")
    private String code;

    @Column(name = "photo")
    private String photo;

    public Role getRole() {
        return isModerator == 1 ? Role.MODERATOR : Role.USER;
    }
}
