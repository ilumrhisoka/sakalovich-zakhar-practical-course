package inno.project.authservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "credential")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credential extends BasicEntity{

    @Column(name = "email")
    @NotNull
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @NotNull
    private UserRole role;

}
