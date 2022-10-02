package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.item.dto.ItemDtoAnswerFull;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    // getting item for the controller
    ItemDtoAnswerFull getItemById(Long userId, Long itemId);

    // getting a DB entity of item from storage
    Item getEntityItemByIdFromStorage(Long itemId);

    // getting a list of items for the controller
    List<ItemDtoAnswerFull> getAllItemsOfUser(Long userId);

    // getting a list of DB entities of items from storage
    List<Item> getAllEntityItemsOfUserFromStorage(Long userId);

    ItemDtoAnswer createItem(Long userId, ItemDto itemDto);

    ItemDtoAnswer updateItem(Long userId, Long itemId, ItemDto itemDto);

    // Getting a list of items by keywords
    List<ItemDtoAnswer> searchForItemsByQueryText(String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);

    boolean isItemExists(Long itemId);
}
