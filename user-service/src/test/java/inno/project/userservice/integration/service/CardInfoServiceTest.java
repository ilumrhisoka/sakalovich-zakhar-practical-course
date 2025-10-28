package inno.project.userservice.integration.service;

import inno.project.userservice.config.TestContainersConfig;
import inno.project.userservice.exception.card.CardInfoNotFoundException;
import inno.project.userservice.exception.card.DuplicateCardNumberException;
import inno.project.userservice.exception.card.ExpiredCardException;
import inno.project.userservice.model.dto.CardInfoRequest;
import inno.project.userservice.model.dto.CardInfoResponse;
import inno.project.userservice.model.dto.UserRequest;
import inno.project.userservice.model.dto.UserResponse;
import inno.project.userservice.repository.CardInfoRepository;
import inno.project.userservice.repository.UserRepository;
import inno.project.userservice.service.CardInfoService;
import inno.project.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class CardInfoServiceTest extends TestContainersConfig {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CardInfoService cardInfoService;

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserResponse testUser;
    private CardInfoRequest testCardRequest;

    @BeforeEach
    void setUp() {
        testUser = userService.createUser(new UserRequest(
                "John", "Doe", "john.doe@example.com", LocalDateTime.now().minusYears(20)
        ));

        testCardRequest = new CardInfoRequest(
                "1234567812345678",
                LocalDateTime.now().plusYears(2),
                testUser.name(),
                testUser.id()
        );
    }

    @Test
    void createCard_success() {
        CardInfoResponse response = cardInfoService.createCard(testCardRequest);

        assertThat(response).isNotNull();
        assertThat(response.number()).isEqualTo(testCardRequest.number());
        assertThat(cardInfoRepository.existsById(response.id())).isTrue();
    }

    @Test
    void createCard_duplicateNumber_throwsException() {
        cardInfoService.createCard(testCardRequest);

        assertThatThrownBy(() -> cardInfoService.createCard(testCardRequest))
                .isInstanceOf(DuplicateCardNumberException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void createCard_expiredCard_throwsException() {
        CardInfoRequest expiredRequest = new CardInfoRequest(
                "8765432187654321",
                LocalDateTime.now().minusDays(1),
                testUser.name(),
                testUser.id()
        );

        assertThatThrownBy(() -> cardInfoService.createCard(expiredRequest))
                .isInstanceOf(ExpiredCardException.class)
                .hasMessageContaining("expired card");
    }

    @Test
    void getCardById_success() {
        CardInfoResponse created = cardInfoService.createCard(testCardRequest);

        CardInfoResponse fetched = cardInfoService.getCardById(created.id());

        assertThat(fetched).isNotNull();
        assertThat(fetched.number()).isEqualTo(testCardRequest.number());
    }

    @Test
    void getCardById_notFound_throwsException() {
        assertThatThrownBy(() -> cardInfoService.getCardById(999L))
                .isInstanceOf(CardInfoNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getCardsByIds_success() {
        CardInfoResponse card1 = cardInfoService.createCard(testCardRequest);
        CardInfoResponse card2 = cardInfoService.createCard(new CardInfoRequest(
                "1111222233334444",
                LocalDateTime.now().plusYears(1),
                testUser.name(),
                testUser.id()
        ));

        List<CardInfoResponse> cards = cardInfoService.getCardsByIds(List.of(card1.id(), card2.id()));
        assertThat(cards).hasSize(2);
    }

    @Test
    void getCardsByIds_emptyList_throwsException() {
        assertThatThrownBy(() -> cardInfoService.getCardsByIds(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be empty");
    }

    @Test
    void getCardsByIds_notFound_throwsException() {
        assertThatThrownBy(() -> cardInfoService.getCardsByIds(List.of(999L, 1000L)))
                .isInstanceOf(CardInfoNotFoundException.class)
                .hasMessageContaining("No cards found");
    }

    @Test
    void updateCard_success() {
        CardInfoResponse created = cardInfoService.createCard(testCardRequest);

        CardInfoRequest updateRequest = new CardInfoRequest(
                "9999888877776666",
                LocalDateTime.now().plusYears(3),
                testUser.name(),
                testUser.id()
        );

        CardInfoResponse updated = cardInfoService.updateCard(created.id(), updateRequest);

        assertThat(updated.number()).isEqualTo(updateRequest.number());
    }

    @Test
    void updateCard_duplicateNumber_throwsException() {
        CardInfoResponse card1 = cardInfoService.createCard(testCardRequest);
        CardInfoResponse card2 = cardInfoService.createCard(new CardInfoRequest(
                "1111222233334444",
                LocalDateTime.now().plusYears(1),
                testUser.name(),
                testUser.id()
        ));

        CardInfoRequest updateRequest = new CardInfoRequest(
                "1234567812345678",
                LocalDateTime.now().plusYears(1),
                testUser.name(),
                testUser.id()
        );

        assertThatThrownBy(() -> cardInfoService.updateCard(card2.id(), updateRequest))
                .isInstanceOf(DuplicateCardNumberException.class);
    }

    @Test
    void updateCard_notFound_throwsException() {
        assertThatThrownBy(() -> cardInfoService.updateCard(999L, testCardRequest))
                .isInstanceOf(CardInfoNotFoundException.class);
    }

    @Test
    void updateCard_expiredCard_throwsException() {
        CardInfoResponse created = cardInfoService.createCard(testCardRequest);
        CardInfoRequest expiredUpdate = new CardInfoRequest(
                "9999000011112222",
                LocalDateTime.now().minusDays(1),
                testUser.name(),
                testUser.id()
        );

        assertThatThrownBy(() -> cardInfoService.updateCard(created.id(), expiredUpdate))
                .isInstanceOf(ExpiredCardException.class);
    }

    @Test
    void deleteCard_success() {
        CardInfoResponse created = cardInfoService.createCard(testCardRequest);

        cardInfoService.deleteCard(created.id());

        assertThat(cardInfoRepository.existsById(created.id())).isFalse();
    }

    @Test
    void deleteCard_notFound_throwsException() {
        assertThatThrownBy(() -> cardInfoService.deleteCard(999L))
                .isInstanceOf(CardInfoNotFoundException.class);
    }

    @Test
    void cacheable_getCardById_hitsCache() {
        CardInfoResponse created = cardInfoService.createCard(testCardRequest);

        CardInfoResponse firstCall = cardInfoService.getCardById(created.id());
        CardInfoResponse secondCall = cardInfoService.getCardById(created.id());

        assertThat(firstCall).isEqualTo(secondCall);

        var cache = cacheManager.getCache(CardInfoService.CARD_CACHE_NAME);
        assertThat(cache).isNotNull();

        CardInfoResponse cached = cache.get(created.id(), CardInfoResponse.class);
        assertThat(cached).isNotNull();
        assertThat(cached).isEqualTo(firstCall);
    }

    @Test
    void cachePut_updateCard_updatesCache() {
        CardInfoResponse created = cardInfoService.createCard(testCardRequest);

        CardInfoRequest updateRequest = new CardInfoRequest(
                "9999888877776666",
                LocalDateTime.now().plusYears(2),
                testUser.name(),
                testUser.id()
        );

        CardInfoResponse updated = cardInfoService.updateCard(created.id(), updateRequest);

        var cache = cacheManager.getCache(CardInfoService.CARD_CACHE_NAME);
        assertThat(cache).isNotNull();

        CardInfoResponse cached = cache.get(created.id(), CardInfoResponse.class);
        assertThat(cached).isNotNull();
        assertThat(cached.number()).isEqualTo(updateRequest.number());
    }

    @Test
    void cacheEvict_deleteCard_removesFromCache() {
        CardInfoResponse created = cardInfoService.createCard(testCardRequest);

        cardInfoService.getCardById(created.id());

        cardInfoService.deleteCard(created.id());

        var cache = cacheManager.getCache(CardInfoService.CARD_CACHE_NAME);
        assertThat(cache).isNotNull();

        CardInfoResponse cached = cache.get(created.id(), CardInfoResponse.class);
        assertThat(cached).isNull();
    }
}
