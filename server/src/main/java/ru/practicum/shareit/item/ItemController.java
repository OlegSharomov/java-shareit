package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.item.dto.ItemDtoAnswerFull;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDtoAnswerFull getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId) {
        log.info("Получен запрос GET/items/{} от пользователя id = {}", itemId, userId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoAnswerFull> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET/items от пользователя id = {}", userId);
        return itemService.getAllItemsOfUser(userId);
    }

    @PostMapping
    public ItemDtoAnswer createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
//                                    @Valid
                                    @RequestBody ItemDto itemDto) {
        log.info("Получен запрос POST/items от пользователя id = {} с переданным телом: {}", userId, itemDto);
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoAnswer updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PATCH/items от пользователя id = {} для изменения вещи id = {} с переданным телом: {}",
                userId, itemId, itemDto);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    // поиск вещей по ключевым словам
    @GetMapping("/search")
    public List<ItemDtoAnswer> searchForItemsByQueryText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam String text) {
        log.info("Получен запрос GET/items/search от пользователя id = {} с текстом запроса: {}", userId, text);
        return itemService.searchForItemsByQueryText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
//                                    @Valid
                                    @RequestBody CommentDto commentDto) {
        log.info("Получен запрос POST/items/{itemId}/comment от пользователя id = {} с отзывом для вещи id = {}, " +
                "текст отзыва: {}", userId, itemId, commentDto);
        return itemService.createComment(userId, itemId, commentDto);
    }
}