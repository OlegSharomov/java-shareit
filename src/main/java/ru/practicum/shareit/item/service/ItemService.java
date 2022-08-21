package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAnswerFull;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(Long userId, Long itemId);

    ItemDtoAnswerFull getItemByIdAndConvertedToAnswer(Long userId, Long itemId);

    List<Item> getAllItemsOfUser(Long userId);

    List<ItemDtoAnswerFull> getAllItemsOfUserAndConvertedToAnswer(Long userId);

    Item createItem(Long userId, Item itemDtoService);

    Item updateItem(Long userId, Long itemId, ItemDto itemDtoService);

    List<Item> searchForItemsByQueryText(String text);

    Comment createComment(Long userId, Long itemId, CommentDto commentDto);
}
