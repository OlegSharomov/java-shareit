package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDtoService;

import java.util.List;

public interface ItemRepository {
    ItemDtoService getItemByIdFromStorage(Long userId, Long itemId);

    List<ItemDtoService> getAllItemsOfUserFromStorage(Long userId);

    ItemDtoService createItemInStorage(Long userId, ItemDtoService itemDtoService);

    ItemDtoService updateItemInStorage(Long userId, Long itemId, ItemDtoService itemDtoService);

    List<ItemDtoService> searchForItemsByQueryTextFromStorage(String[] words);
}
