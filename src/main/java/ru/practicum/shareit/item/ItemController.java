package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDtoController;
import ru.practicum.shareit.item.dto.ItemDtoControllerForAnswer;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    /*Просмотр информации о конкретной вещи по её идентификатору. Эндпойнт GET /items/{itemId}.
    Информацию о вещи может просмотреть любой пользователь.*/
    @GetMapping("/{itemId}")
    public ItemDtoController getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return service.getItemById(userId, itemId);
    }

    /*Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой. Эндпойнт GET /items.*/
    @GetMapping
    public List<Item> getAllItems() {
        return service.getAllItems();
    }

    /*Добавление новой вещи. Будет происходить по эндпойнту POST /items. На вход поступает объект ItemDto.
    userId в заголовке X-Sharer-User-Id — это идентификатор пользователя, который добавляет вещь.
    Именно этот пользователь — владелец вещи. Идентификатор владельца будет поступать на вход в каждом из запросов,
    рассмотренных далее.*/
    @PostMapping
    public ItemDtoControllerForAnswer createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody ItemDtoController itemDtoController) {
        ItemDtoService itemDtoService = ItemDtoMapper.itemDtoControllerToItemDtoService(itemDtoController);
        ItemDtoService itemDtoServiceForAnswer =  service.createItem(userId, itemDtoService);
        return ItemDtoMapper.itemDtoServiceToItemDtoControllerForAnswer(itemDtoServiceForAnswer);
    }

    /*Редактирование вещи. Эндпойнт PATCH /items/{itemId}. Изменить можно название, описание и статус доступа к аренде.
    Редактировать вещь может только её владелец.*/
    @PatchMapping("/{itemId}")
    public ItemDtoController updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long itemId,
                                        @RequestBody ItemDtoController itemDto) {
        return service.updateItem(userId, itemId, itemDto);
    }

    /*Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст, и система ищет вещи,
    содержащие этот текст в названии или описании. Происходит по эндпойнту /items/search?text={text},
    в text передаётся текст для поиска. Проверьте, что поиск возвращает только доступные для аренды вещи*/
    @GetMapping("/search")
    public List<ItemDtoController> findItems(@RequestParam String text) {
        return service.findItems(text);
    }
}
