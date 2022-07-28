package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.OwnerVerificationException;
import ru.practicum.shareit.item.dto.ItemDtoController;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private static Long id = 0L;

    private static Long getNewItemId() {
        return ++id;
    }

    public ItemDtoService getItemByIdFromStorage(Long userId, Long itemId) {
        return ItemDtoMapper.itemToItemDtoService(items.get(itemId));
    }

    public List<ItemDtoService> getAllItemsFromStorage(Long userId) {
        return items.values().stream()
                .filter(x -> x.getOwner().equals(userId))
                .map(ItemDtoMapper::itemToItemDtoService)
                .collect(Collectors.toList());
    }

    public ItemDtoService createItemInStorage(Long userId, ItemDtoService itemDtoService) {
        itemDtoService.setId(getNewItemId());
        itemDtoService.setOwner(userId);
        Item item = ItemDtoMapper.itemDtoServiceToItem(itemDtoService);
        items.put(item.getId(), item);
        return ItemDtoMapper.itemToItemDtoService(item);
    }

    public ItemDtoService updateItemInStorage(Long userId, Long itemId, ItemDtoService itemDtoService) {
        Item itemFromRepository = items.get(itemId);
        if(!itemFromRepository.getOwner().equals(userId)){
            throw new OwnerVerificationException("Указан не верный пользователь вещи");
        }
        if (itemDtoService.getName() != null && !itemDtoService.getName().equals(itemFromRepository.getName())) {
            itemFromRepository.setName(itemDtoService.getName());
        }
        if (itemDtoService.getDescription() != null
                && !itemDtoService.getDescription().equals(itemFromRepository.getDescription())) {
            itemFromRepository.setDescription(itemDtoService.getDescription());
        }
        if (itemDtoService.getAvailable() != null
                && !itemDtoService.getAvailable().equals(itemFromRepository.getAvailable())) {
            itemFromRepository.setAvailable(itemDtoService.getAvailable());
        }
        return ItemDtoMapper.itemToItemDtoService(itemFromRepository);
    }

    public List<ItemDtoService> findItemsFromStorage(String[] words) {
        return items.values().stream()
                .filter(e -> containsWords(e, words) && e.getAvailable().equals(true))
                .map(e -> ItemDtoMapper.itemToItemDtoService(e))
                .collect(Collectors.toList());
    }

    private static boolean containsWords(Item item, String[] words){
        return Arrays.stream(words)
                .anyMatch(w -> item.getName().toLowerCase(Locale.ROOT).contains(w.toLowerCase())
                        || item.getDescription().toLowerCase(Locale.ROOT).contains(w.toLowerCase()));
    }
}
