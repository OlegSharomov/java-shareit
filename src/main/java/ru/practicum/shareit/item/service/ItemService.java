package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.item.dto.ItemDtoAnswerFull;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    // получение вещи для контроллера
    ItemDtoAnswerFull getItemById(Long userId, Long itemId);

    // получение сущности БД из хранилища
    Item getEntityItemByIdFromStorage(Long userId, Long itemId);

    // получение списка вещей для контроллера
    List<ItemDtoAnswerFull> getAllItemsOfUser(Long userId);

    // получение списка сущностей БД из хранилища
    List<Item> getAllEntityItemsOfUserFromStorage(Long userId);

    ItemDtoAnswer createItem(Long userId, ItemDto itemDto);

    ItemDtoAnswer updateItem(Long userId, Long itemId, ItemDto itemDto);

    // Получение списка вещей по ключевым словам
    List<ItemDtoAnswer> searchForItemsByQueryText(String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);

    boolean isItemExists(Long itemId);
}
