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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
class ItemController {
    private final ItemService service;
    private final ItemMapper itemMapper;

    @GetMapping("/{itemId}")
    public ItemDtoAnswer getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long itemId) {
        log.info("Получен запрос GET/items/{} от пользователя id = {}", itemId, userId);
        return itemMapper.toItemDtoAnswer(service.getItemById(userId, itemId));
    }

    @GetMapping
    public List<ItemDtoAnswer> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET/items от пользователя id = {}", userId);
        return service.getAllItemsOfUser(userId).stream()
                .map(itemMapper::toItemDtoAnswer).collect(Collectors.toList());
    }

    @PostMapping
    public ItemDtoAnswer createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос POST/items от пользователя id = {} с переданным телом: {}", userId, itemDto);
        Item item = itemMapper.toItem(itemDto);
        Item itemForAnswer = service.createItem(userId, item);
        return itemMapper.toItemDtoAnswer(itemForAnswer);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoAnswer updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PATCH/items от пользователя id = {} для изменения вещи id = {} с переданным телом: {}",
                userId, itemId, itemDto);
        Item item = itemMapper.toItem(itemDto);
        Item itemForAnswer = service.updateItem(userId, itemId, item);
        return itemMapper.toItemDtoAnswer(itemForAnswer);
    }

    @GetMapping("/search")
    public List<ItemDtoAnswer> searchForItemsByQueryText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam String text) {
        log.info("Получен запрос GET/items/search от пользователя id = {} с текстом запроса: {}", userId, text);
        return service.searchForItemsByQueryText(text).stream()
                .map(itemMapper::toItemDtoAnswer)
                .collect(Collectors.toList());
    }
}