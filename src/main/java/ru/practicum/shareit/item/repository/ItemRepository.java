package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item getItemByIdFromStorage(Long userId, Long itemId);

    List<Item> getAllItemsOfUserFromStorage(Long userId);

    Item createItemInStorage(Long userId, Item itemDtoService);

    Item updateItemInStorage(Long userId, Long itemId, Item itemDtoService);

    List<Item> searchForItemsByQueryTextFromStorage(String[] words);
}
