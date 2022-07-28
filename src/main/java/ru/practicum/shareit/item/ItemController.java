package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoController;
import ru.practicum.shareit.item.dto.ItemDtoControllerForAnswer;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    /*Просмотр информации о конкретной вещи по её идентификатору. Эндпойнт GET /items/{itemId}.
    Информацию о вещи может просмотреть любой пользователь.*/
    @GetMapping("/{itemId}")
    public ItemDtoControllerForAnswer getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return ItemDtoMapper.itemDtoServiceToItemDtoControllerForAnswer(service.getItemById(userId, itemId));
    }

    /*Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой. Эндпойнт GET /items. */
    @GetMapping
    public List<ItemDtoControllerForAnswer> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllItems(userId).stream()
                .map(ItemDtoMapper::itemDtoServiceToItemDtoControllerForAnswer).collect(Collectors.toList());
    }

    /*Добавление новой вещи. Будет происходить по эндпойнту POST /items. На вход поступает объект ItemDto.
    userId в заголовке X-Sharer-User-Id — это идентификатор пользователя, который добавляет вещь.
    Именно этот пользователь — владелец вещи. Идентификатор владельца будет поступать на вход в каждом из запросов,
    рассмотренных далее.*/
    @PostMapping
    public ItemDtoControllerForAnswer createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody ItemDtoController itemDtoController) {
        ItemDtoService itemDtoService = ItemDtoMapper.itemDtoControllerToItemDtoService(itemDtoController);
        ItemDtoService itemDtoServiceForAnswer = service.createItem(userId, itemDtoService);
        return ItemDtoMapper.itemDtoServiceToItemDtoControllerForAnswer(itemDtoServiceForAnswer);
    }

    /*Редактирование вещи. Эндпойнт PATCH /items/{itemId}. Изменить можно название, описание и статус доступа к аренде.
    Редактировать вещь может только её владелец.*/
    @PatchMapping("/{itemId}")
    public ItemDtoControllerForAnswer updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody ItemDtoController itemDtoController) {
        ItemDtoService itemDtoService = ItemDtoMapper.itemDtoControllerToItemDtoService(itemDtoController);
        ItemDtoService itemDtoServiceForAnswer = service.updateItem(userId, itemId, itemDtoService);
        return ItemDtoMapper.itemDtoServiceToItemDtoControllerForAnswer(itemDtoServiceForAnswer);
    }

    /*Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст, и система ищет вещи,
    содержащие этот текст в названии или описании. Происходит по эндпойнту /items/search?text={text},
    в text передаётся текст для поиска. Проверьте, что поиск возвращает только доступные для аренды вещи*/
    @GetMapping("/search")
    public List<ItemDtoControllerForAnswer> findItems(@RequestParam String text) {
        return service.findItems(text).stream()
                .map(ItemDtoMapper::itemDtoServiceToItemDtoControllerForAnswer)
                .collect(Collectors.toList());
    }
}
