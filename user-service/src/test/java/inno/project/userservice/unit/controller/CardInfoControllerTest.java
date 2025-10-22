package inno.project.userservice.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inno.project.userservice.controller.CardInfoController;
import inno.project.userservice.exception.GlobalExceptionHandler;
import inno.project.userservice.exception.card.CardInfoNotFoundException;
import inno.project.userservice.model.dto.CardInfoRequest;
import inno.project.userservice.model.dto.CardInfoResponse;
import inno.project.userservice.service.CardInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardInfoControllerTest {

    @Mock
    private CardInfoService cardInfoService;

    @InjectMocks
    private CardInfoController cardInfoController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private final LocalDateTime MOCK_EXPIRY_DATE = LocalDateTime.of(2030, 12, 31, 0, 0);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardInfoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    void createCard_ReturnsCreatedCard() throws Exception {
        CardInfoRequest request = new CardInfoRequest("1234567890123456", MOCK_EXPIRY_DATE, "John Doe", 1L);
        CardInfoResponse response = createMockCardResponse(1L, "1234567890123456", "John Doe", MOCK_EXPIRY_DATE);

        Mockito.when(cardInfoService.createCard(any(CardInfoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("1234567890123456"))
                .andExpect(jsonPath("$.holder").value("John Doe"));
    }

    @Test
    void getCard_ReturnsCard() throws Exception {
        CardInfoResponse response = createMockCardResponse(1L, "1234567890123456", "John Doe", MOCK_EXPIRY_DATE);

        Mockito.when(cardInfoService.getCardById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/cards/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("1234567890123456"))
                .andExpect(jsonPath("$.holder").value("John Doe"));
    }

    @Test
    void getCards_ReturnsListOfCards() throws Exception {
        CardInfoResponse card1 = createMockCardResponse(1L, "1111222233334444", "Alice", MOCK_EXPIRY_DATE);
        CardInfoResponse card2 = createMockCardResponse(2L, "5555666677778888", "Bob", MOCK_EXPIRY_DATE);

        Mockito.when(cardInfoService.getCardsByIds(List.of(1L, 2L))).thenReturn(List.of(card1, card2));

        mockMvc.perform(get("/api/cards")
                        .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void updateCard_ReturnsUpdatedCard() throws Exception {
        CardInfoRequest request = new CardInfoRequest("9999888877776666", MOCK_EXPIRY_DATE, "John Doe", 1L);
        CardInfoResponse response = createMockCardResponse(1L, "9999888877776666", "Jane Doe", MOCK_EXPIRY_DATE);

        Mockito.when(cardInfoService.updateCard(eq(1L), any(CardInfoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("9999888877776666"))
                .andExpect(jsonPath("$.holder").value("Jane Doe"));
    }

    @Test
    void deleteCard_ReturnsNoContent() throws Exception {
        Mockito.doNothing().when(cardInfoService).deleteCard(1L);

        mockMvc.perform(delete("/api/cards/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCard_ReturnsNotFound() throws Exception {
        Mockito.when(cardInfoService.getCardById(99L))
                .thenThrow(new CardInfoNotFoundException("Card not found"));

        mockMvc.perform(get("/api/cards/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCard_ReturnsNotFound() throws Exception {
        CardInfoRequest request = new CardInfoRequest("0000111122223333", MOCK_EXPIRY_DATE, "do", 99L);

        Mockito.when(cardInfoService.updateCard(eq(99L), any(CardInfoRequest.class)))
                .thenThrow(new CardInfoNotFoundException("Card not found"));

        mockMvc.perform(put("/api/cards/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCard_ReturnsNotFound() throws Exception {
        Mockito.doThrow(new CardInfoNotFoundException("Card not found"))
                .when(cardInfoService).deleteCard(99L);

        mockMvc.perform(delete("/api/cards/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCard_InvalidRequest_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private CardInfoResponse createMockCardResponse(Long id, String cardNumber, String cardHolder, LocalDateTime expiryDate) {
        return CardInfoResponse.builder()
                .id(id)
                .number(cardNumber)
                .holder(cardHolder)
                .expirationDate(expiryDate)
                .build();
    }
}
