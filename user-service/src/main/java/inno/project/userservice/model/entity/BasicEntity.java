package inno.project.userservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class BasicEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id", unique = true)
    private Long id;

    @Version
    @NotNull
    @Min(value = 1)
    @Column(nullable = false, name = "version")
    private Long version = 1L;


    @CreationTimestamp
    @NotNull
    @Column(nullable = false, name = "created_dttm")
    private LocalDateTime createdAt = LocalDateTime.now();


    @UpdateTimestamp
    @NotNull
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();


}