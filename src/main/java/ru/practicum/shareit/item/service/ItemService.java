package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoService;

import java.util.List;

public interface ItemService {
    ItemDtoService getItemById(Long userId, Long itemId);

    List<ItemDtoService> getAllItemsOfUser(Long userId);

    ItemDtoService createItem(Long userId, ItemDtoService itemDtoService);

    ItemDtoService updateItem(Long userId, Long itemId, ItemDtoService itemDtoService);

    List<ItemDtoService> searchForItemsByQueryText(String text);
}
