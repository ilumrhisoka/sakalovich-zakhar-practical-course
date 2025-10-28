package inno.project.authservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id", unique = true, columnDefinition = "BIGINT")
    private Long id;

    @Version
    @NotNull
    @Column(nullable = false, name = "version")
    private Long version = 1L;

    @NotNull
    @Column(nullable = false, name = "created_dttm")
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @NotNull
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}