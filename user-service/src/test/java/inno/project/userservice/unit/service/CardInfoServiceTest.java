package inno.project.userservice.unit.service;

import inno.project.userservice.model.dto.UserResponse;
import inno.project.userservice.exception.card.CardInfoNotFoundException;
import inno.project.userservice.exception.card.DuplicateCardNumberException;
import inno.project.userservice.exception.card.ExpiredCardException;
import inno.project.userservice.mapper.CardInfoMapper;
import inno.project.userservice.model.dto.CardInfoRequest;
import inno.project.userservice.model.dto.CardInfoResponse;
import inno.project.userservice.model.entity.CardInfo;
import inno.project.userservice.model.entity.User;
import inno.project.userservice.repository.CardInfoRepository;
import inno.project.userservice.service.CardInfoService;
import inno.project.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceTest {

    @Mock
    private CardInfoRepository cardInfoRepository;

    @Mock
    private UserService userService;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @InjectMocks
    private CardInfoService cardInfoService;

    private User user;
    private CardInfoRequest cardRequest;
    private CardInfo cardEntity;
    private CardInfoResponse cardResponse;

    private final LocalDateTime MOCK_EXPIRATION = LocalDateTime.now().plusYears(1);

    UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john@example.com");
        user.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));

        userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .build();

        cardRequest = new CardInfoRequest("1234567890123456", MOCK_EXPIRATION, "John Doe", 1L);

        cardEntity = new CardInfo();
        cardEntity.setId(1L);
        cardEntity.setNumber("1234567890123456");
        cardEntity.setHolder("John Doe");
        cardEntity.setExpirationDate(MOCK_EXPIRATION);
        cardEntity.setUser(user);

        cardResponse = CardInfoResponse.builder()
                .id(1L)
                .userId(user.getId())
                .number("1234567890123456")
                .holder("John Doe")
                .expirationDate(MOCK_EXPIRATION)
                .build();

        lenient().when(userService.getUserById(anyLong())).thenReturn(userResponse); // возвращает DTO
        lenient().when(userService.getEntityById(anyLong())).thenReturn(user);        // возвращает entity
    }

    @Test
    void createCard_Success() {
        when(cardInfoRepository.findByNumber(cardRequest.number())).thenReturn(Optional.empty());
        when(cardInfoMapper.toEntity(cardRequest)).thenReturn(cardEntity);
        when(cardInfoRepository.save(cardEntity)).thenReturn(cardEntity);
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardResponse);

        CardInfoResponse result = cardInfoService.createCard(cardRequest);

        assertEquals(cardResponse.id(), result.id());
        verify(cardInfoRepository).save(cardEntity);
    }

    @Test
    void createCard_DuplicateNumber_ThrowsException() {
        when(cardInfoRepository.findByNumber(cardRequest.number())).thenReturn(Optional.of(cardEntity));

        assertThrows(DuplicateCardNumberException.class, () -> cardInfoService.createCard(cardRequest));
        verify(cardInfoRepository, never()).save(any());
    }

    @Test
    void createCard_ExpiredCard_ThrowsException() {
        CardInfoRequest expiredRequest = new CardInfoRequest("1111222233334444", LocalDateTime.now().minusDays(1), "John Doe", 1L);

        assertThrows(ExpiredCardException.class, () -> cardInfoService.createCard(expiredRequest));
        verify(cardInfoRepository, never()).save(any());
    }

    @Test
    void getCardById_Success() {
        when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardResponse);

        CardInfoResponse result = cardInfoService.getCardById(1L);

        assertEquals(cardResponse.id(), result.id());
    }

    @Test
    void getCardById_NotFound() {
        when(cardInfoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CardInfoNotFoundException.class, () -> cardInfoService.getCardById(99L));
    }

    @Test
    void getCardsByIds_Success() {
        CardInfo card2 = new CardInfo();
        card2.setId(2L);
        card2.setNumber("1111222233334444");
        card2.setHolder("Jane Doe");
        card2.setExpirationDate(MOCK_EXPIRATION);
        card2.setUser(user);

        CardInfoResponse response2 = CardInfoResponse.builder()
                .id(2L)
                .userId(user.getId())
                .number("1111222233334444")
                .holder("Jane Doe")
                .expirationDate(MOCK_EXPIRATION)
                .build();

        List<Long> ids = List.of(1L, 2L);

        when(cardInfoRepository.findAllById(ids)).thenReturn(List.of(cardEntity, card2));
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardResponse);
        when(cardInfoMapper.toDto(card2)).thenReturn(response2);

        List<CardInfoResponse> result = cardInfoService.getCardsByIds(ids);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }

    @Test
    void getCardsByIds_EmptyList_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cardInfoService.getCardsByIds(List.of()));
    }

    @Test
    void getCardsByIds_NotFound_ThrowsException() {
        List<Long> ids = List.of(99L);
        when(cardInfoRepository.findAllById(ids)).thenReturn(List.of());

        assertThrows(CardInfoNotFoundException.class, () -> cardInfoService.getCardsByIds(ids));
    }

    @Test
    void updateCard_Success() {
        CardInfoRequest updateRequest = new CardInfoRequest("9999888877776666", MOCK_EXPIRATION, "John Doe", 1L);
        CardInfo updatedEntity = new CardInfo();
        updatedEntity.setId(1L);
        updatedEntity.setNumber("9999888877776666");
        updatedEntity.setHolder("John Doe");
        updatedEntity.setExpirationDate(MOCK_EXPIRATION);
        updatedEntity.setUser(user);

        CardInfoResponse updatedResponse = CardInfoResponse.builder()
                .id(1L)
                .userId(user.getId())
                .number("9999888877776666")
                .holder("John Doe")
                .expirationDate(MOCK_EXPIRATION)
                .build();

        when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardInfoRepository.findByNumber(updateRequest.number())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            cardEntity.setNumber(updateRequest.number());
            return null;
        }).when(cardInfoMapper).update(cardEntity, updateRequest);
        when(cardInfoRepository.save(cardEntity)).thenReturn(updatedEntity);
        when(cardInfoMapper.toDto(updatedEntity)).thenReturn(updatedResponse);

        CardInfoResponse result = cardInfoService.updateCard(1L, updateRequest);

        assertEquals("9999888877776666", result.number());
    }

    @Test
    void updateCard_NotFound() {
        when(cardInfoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CardInfoNotFoundException.class, () -> cardInfoService.updateCard(99L, cardRequest));
    }

    @Test
    void updateCard_DuplicateNumber_ThrowsException() {
        CardInfo otherCard = new CardInfo();
        otherCard.setId(2L);
        otherCard.setNumber("9999888877776666");
        otherCard.setUser(user);

        CardInfoRequest requestWithDuplicateNumber = new CardInfoRequest(
                "9999888877776666", MOCK_EXPIRATION, "John Doe", 1L);

        when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardInfoRepository.findByNumber("9999888877776666")).thenReturn(Optional.of(otherCard));

        assertThrows(DuplicateCardNumberException.class,
                () -> cardInfoService.updateCard(1L, requestWithDuplicateNumber));
    }

    @Test
    void updateCard_ExpiredCard_ThrowsException() {
        CardInfoRequest expiredRequest = new CardInfoRequest("1111222233334444", LocalDateTime.now().minusDays(1), "John Doe", 1L);
        when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(cardEntity));

        assertThrows(ExpiredCardException.class, () -> cardInfoService.updateCard(1L, expiredRequest));
    }

    @Test
    void deleteCard_Success() {
        when(cardInfoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cardInfoRepository).deleteById(1L);

        assertDoesNotThrow(() -> cardInfoService.deleteCard(1L));
        verify(cardInfoRepository).deleteById(1L);
    }

    @Test
    void deleteCard_NotFound() {
        when(cardInfoRepository.existsById(99L)).thenReturn(false);

        assertThrows(CardInfoNotFoundException.class, () -> cardInfoService.deleteCard(99L));
    }
}
