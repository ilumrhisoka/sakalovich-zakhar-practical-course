package inno.project.userservice.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BasicEntity {

    @Column(name = "name", length = 30)
    @NotNull
    @Size(max = 30)
    private String name;

    @Column(name = "surname", length = 30)
    @NotNull
    @Size(max = 30)
    private String surname;

    @Column(name = "birth_date")
    @NotNull
    private LocalDateTime birthDate;

    @Column(name = "email", length = 30, unique = true)
    @NotNull
    @Size(max = 30)
    @Email
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CardInfo> cards = new ArrayList<>();

}