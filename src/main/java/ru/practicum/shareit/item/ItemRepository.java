package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDtoController;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {
    private Map<Long, Item> items = new HashMap<>();
    private static Long id = 0L;

    private static Long getNewItemId() {
        return ++id;
    }
    public ItemDtoController getItemByIdFromStorage(Long userId, Long itemId) {
        return null;
    }

    public List<Item> getAllItemsFromStorage() {
        return null;
    }

    public ItemDtoService createItemInStorage(Long userId, ItemDtoService itemDtoService) {
        itemDtoService.setId(getNewItemId());
        Item item = ItemDtoMapper.itemDtoServiceToItem(itemDtoService);
        items.put(item.getId(), item);
        return ItemDtoMapper.itemToItemDtoService(item);
    }

    public ItemDtoController updateItemInStorage(Long userId, Long itemId, ItemDtoController itemDto) {
        return null;
    }

    public List<ItemDtoController> findItemsFromStorage(String text) {
        return null;
    }
}
