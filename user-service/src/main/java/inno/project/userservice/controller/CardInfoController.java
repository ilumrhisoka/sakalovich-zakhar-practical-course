package inno.project.userservice.controller;

import inno.project.userservice.model.dto.CardInfoRequest;
import inno.project.userservice.model.dto.CardInfoResponse;
import inno.project.userservice.service.CardInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Card Information", description = "Endpoints for cards management")
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @PreAuthorize("hasRole('ADMIN') or #cardInfoRequest.userId.toString() == principal.username")
    @PostMapping
    @Operation(summary = "Create a new card",
            description = "Creates a new card and returns the card information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
    })
    public ResponseEntity<CardInfoResponse> createCard(@Validated @RequestBody CardInfoRequest cardInfoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoService.createCard(cardInfoRequest));
    }

    @PreAuthorize("hasRole('ADMIN') or @cardInfoService.getOwnerId(#id) == principal.username")
    @GetMapping("/{id}")
    @Operation(summary = "Get card by ID",
            description = "Retrieves card information for the specified card ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
    })
    public ResponseEntity<CardInfoResponse> getCard(@PathVariable long id) {
        return ResponseEntity.ok(cardInfoService.getCardById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get cards by IDs",
            description = "Retrieves information for multiple cards based on their IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "One or more cards not found"),
    })
    public ResponseEntity<List<CardInfoResponse>> getCards(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(cardInfoService.getCardsByIds(ids));
    }

    @PreAuthorize("hasRole('ADMIN') or @cardInfoService.getOwnerId(#id) == principal.username")
    @PutMapping("/{id}")
    @Operation(summary = "Update card by ID",
            description = "Updates the information of a specified card.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card updated successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
    })
    public ResponseEntity<CardInfoResponse> updateCard(@PathVariable long id,
                                                   @RequestBody CardInfoRequest cardInfoRequest) {
        return ResponseEntity.ok(cardInfoService.updateCard(id, cardInfoRequest));
    }

    @PreAuthorize("hasRole('ADMIN') or @cardInfoService.getOwnerId(#id) == principal.username")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete card by ID",
            description = "Deletes the card specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
    })
    public ResponseEntity<CardInfoResponse> deleteCard(@PathVariable long id) {
        cardInfoService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
