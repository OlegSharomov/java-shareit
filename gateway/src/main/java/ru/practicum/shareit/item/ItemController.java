package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemReqDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@NotNull(message = "The X-Sharer-User-Id is missing")
                                              @Positive(message = "id must be positive")
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Positive(message = "id must be positive")
                                              @PathVariable Long itemId) {
        log.info("Получен запрос GET/items/{} от пользователя id = {}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfUser(@NotNull(message = "The X-Sharer-User-Id is missing")
                                                    @Positive(message = "id must be positive")
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET/items от пользователя id = {}", userId);
        return itemClient.getAllItemsOfUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@NotNull(message = "The X-Sharer-User-Id is missing")
                                             @Positive(message = "id must be positive")
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemReqDto itemDto) {
        log.info("Получен запрос POST/items от пользователя id = {} с переданным телом: {}", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@NotNull(message = "The X-Sharer-User-Id is missing")
                                             @Positive(message = "id must be positive")
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Positive(message = "id must be positive")
                                             @PathVariable Long itemId,
                                             @RequestBody ItemReqDto itemDto) {
        log.info("Получен запрос PATCH/items от пользователя id = {} для изменения вещи id = {} с переданным телом: {}",
                userId, itemId, itemDto);
        if (itemDto.getId() != null && !itemDto.getId().equals(itemId)) {
            throw new ValidationException("Id вещи изменять нельзя");
        }
        if (itemDto.getName() != null && itemDto.getName().trim().isEmpty()) {
            throw new ValidationException("Название вещи не должно быть пустым");
        }
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    // поиск вещей по ключевым словам
    @GetMapping("/search")
    public ResponseEntity<Object> searchForItemsByQueryText(@NotNull(message = "The X-Sharer-User-Id is missing")
                                                            @Positive(message = "id must be positive")
                                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam String text) {
        log.info("Получен запрос GET/items/search от пользователя id = {} с текстом запроса: {}", userId, text);
        return itemClient.searchForItemsByQueryText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@NotNull(message = "The X-Sharer-User-Id is missing")
                                                @Positive(message = "id must be positive")
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Positive(message = "id must be positive")
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentRequestDto commentDto) {
        log.info("Получен запрос POST/items/{itemId}/comment от пользователя id = {} с отзывом для вещи id = {}, " +
                "текст отзыва: {}", userId, itemId, commentDto);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
