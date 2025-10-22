package inno.project.authservice.repository;

import inno.project.authservice.model.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialsRepository extends JpaRepository<Credential, Long> {
    Optional<Credential> findByEmail(String email);

    boolean existsByEmail(String email);
}
