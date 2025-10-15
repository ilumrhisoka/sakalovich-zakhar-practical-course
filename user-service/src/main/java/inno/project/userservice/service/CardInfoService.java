package inno.project.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import inno.project.userservice.exception.card.CardInfoNotFoundException;
import inno.project.userservice.exception.card.DuplicateCardNumberException;
import inno.project.userservice.exception.card.ExpiredCardException;
import inno.project.userservice.mapper.CardInfoMapper;
import inno.project.userservice.model.dto.CardInfoRequest;
import inno.project.userservice.model.dto.CardInfoResponse;
import inno.project.userservice.model.entity.CardInfo;
import inno.project.userservice.repository.CardInfoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CardInfoService  {

    public static final String CARD_CACHE_NAME = "cardInfo";

    private final CardInfoRepository cardInfoRepository;
    private final UserService userService;
    private final CardInfoMapper cardInfoMapper;

    @Transactional
    @CachePut(value = CARD_CACHE_NAME, key = "#result.id()")
    public CardInfoResponse createCard(CardInfoRequest request) {
        if (cardInfoRepository.findByNumber(request.number()).isPresent()) {
            throw new DuplicateCardNumberException("Card with number " + request.number() + " already exists");
        }
        if (request.expirationDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredCardException("Cannot register an expired card");
        }
        userService.getUserById(request.userId());
        CardInfo saved = cardInfoRepository.save(cardInfoMapper.toEntity(request));
        return cardInfoMapper.toDto(saved);
    }

    @Cacheable(value = CARD_CACHE_NAME, key = "#id")
    public CardInfoResponse getCardById(Long id) {
        return cardInfoRepository.findById(id).map(cardInfoMapper::toDto)
                .orElseThrow(() -> new CardInfoNotFoundException("Card with id " + id + " not found"));
    }

    public List<CardInfoResponse> getCardsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("List of IDs must not be empty");
        }
        List<CardInfo> cards = cardInfoRepository.findAllById(ids);
        if (cards.isEmpty()) {
            throw new CardInfoNotFoundException("No cards found for given ids: " + ids);
        }
        return cards.stream().map(cardInfoMapper::toDto).toList();
    }

    @Transactional
    @CachePut(value = CARD_CACHE_NAME, key = "#id")
    public CardInfoResponse updateCard(Long id, CardInfoRequest request) {
        CardInfo card = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardInfoNotFoundException("Card not found: " + id));

        if (!card.getNumber().equals(request.number())
                && cardInfoRepository.findByNumber(request.number()).isPresent()) {
            throw new DuplicateCardNumberException(request.number());
        }

        if (request.expirationDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredCardException("Cannot register an expired card");
        }
        cardInfoMapper.update(card, request);
        if (!card.getUser().getId().equals(request.userId())) {
            card.setUser(userService.getEntityById(request.userId()));
        }
        CardInfo saved = cardInfoRepository.save(card);
        return cardInfoMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = CARD_CACHE_NAME, key = "#id")
    public void deleteCard(Long id) {
        if (!cardInfoRepository.existsById(id)) {
            throw new CardInfoNotFoundException("Card not found for id: " + id);
        }
        cardInfoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public String getOwnerId(long cardId) {
        return cardInfoRepository.findById(cardId)
                .map(card -> card.getUser().getId())
                .orElseThrow(() -> new CardInfoNotFoundException("Card with ID: " + cardId + " not found."))
                .toString();
    }
}
