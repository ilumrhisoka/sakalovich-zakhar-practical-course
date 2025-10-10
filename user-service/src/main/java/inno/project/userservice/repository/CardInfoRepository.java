package inno.project.userservice.repository;

import inno.project.userservice.model.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    Optional<CardInfo> findByNumber(String cardNumber);

}