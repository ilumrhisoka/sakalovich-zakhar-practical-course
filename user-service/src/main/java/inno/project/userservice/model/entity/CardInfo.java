package inno.project.userservice.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Table(name = "card_info")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardInfo extends BasicEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "fk_card_user"))
    @NotNull
    @JsonBackReference
    private User user;

    @Column(name = "number", length = 16)
    @NotNull
    @Pattern(regexp = "^[0-9]{16}$")
    private String number;

    @Column(name = "holder")
    @NotNull
    private String holder;

    @Column(name = "expiration_date")
    @NotNull
    private LocalDateTime expirationDate;
}