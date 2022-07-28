package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoController;
import ru.practicum.shareit.item.dto.ItemDtoControllerForAnswer;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/* В задании было создать DTO для работы с контроллерами, но наш наставник предложил, как вариант, использовать
 * несколько DTO объектов для работы с controllers и service классами, а в дальнейшем реализовать Dao для БД.
 * И я подумал, что это отличный вариант! Возможно реализация не везде сделана корректно, с удовольствием жду критики! */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
class ItemController {
    private final ItemService service;

    @GetMapping("/{itemId}")
    public ItemDtoControllerForAnswer getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long itemId) {
        log.info("Получен запрос GET/items/{} от пользователя id = {}", itemId, userId);
        return ItemDtoMapper.itemDtoServiceToItemDtoControllerForAnswer(service.getItemById(userId, itemId));
    }

    @GetMapping
    public List<ItemDtoControllerForAnswer> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET/items от пользователя id = {}", userId);
        return service.getAllItemsOfUser(userId).stream()
                .map(ItemDtoMapper::itemDtoServiceToItemDtoControllerForAnswer).collect(Collectors.toList());
    }

    @PostMapping
    public ItemDtoControllerForAnswer createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody ItemDtoController itemDtoController) {
        log.info("Получен запрос POST/items от пользователя id = {} с переданным телом: {}", userId, itemDtoController);
        ItemDtoService itemDtoService = ItemDtoMapper.itemDtoControllerToItemDtoService(itemDtoController);
        ItemDtoService itemDtoServiceForAnswer = service.createItem(userId, itemDtoService);
        return ItemDtoMapper.itemDtoServiceToItemDtoControllerForAnswer(itemDtoServiceForAnswer);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoControllerForAnswer updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody ItemDtoController itemDtoController) {
        log.info("Получен запрос PATCH/items от пользователя id = {} для изменения вещи id = {} с переданным телом: {}",
                userId, itemId, itemDtoController);
        ItemDtoService itemDtoService = ItemDtoMapper.itemDtoControllerToItemDtoService(itemDtoController);
        ItemDtoService itemDtoServiceForAnswer = service.updateItem(userId, itemId, itemDtoService);
        return ItemDtoMapper.itemDtoServiceToItemDtoControllerForAnswer(itemDtoServiceForAnswer);
    }

    @GetMapping("/search")
    public List<ItemDtoControllerForAnswer> searchForItemsByQueryText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                      @RequestParam String text) {
        log.info("Получен запрос GET/items/search от пользователя id = {} с текстом запроса: {}", userId, text);
        return service.searchForItemsByQueryText(text).stream()
                .map(ItemDtoMapper::itemDtoServiceToItemDtoControllerForAnswer)
                .collect(Collectors.toList());
    }
}
