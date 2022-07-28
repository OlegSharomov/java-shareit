package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDtoService getItemById(Long userId, Long itemId) {
        return itemRepository.getItemByIdFromStorage(userId, itemId);
    }

    @Override
    public List<ItemDtoService> getAllItemsOfUser(Long userId) {
        return itemRepository.getAllItemsOfUserFromStorage(userId);
    }

    @Override
    public ItemDtoService createItem(Long userId, ItemDtoService itemDtoService) {
        if (!userRepository.isUserExistsById(userId)) {
            throw new UserNotFoundException("Пользователь с переданным id не найден");
        }
        return itemRepository.createItemInStorage(userId, itemDtoService);
    }

    @Override
    public ItemDtoService updateItem(Long userId, Long itemId, ItemDtoService itemDtoService) {
        if (!userRepository.isUserExistsById(userId)) {
            throw new UserNotFoundException("Пользователь с переданным id не найден");
        }
        return itemRepository.updateItemInStorage(userId, itemId, itemDtoService);
    }

    @Override
    public List<ItemDtoService> searchForItemsByQueryText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        String[] words = text.split(" ");
        return itemRepository.searchForItemsByQueryTextFromStorage(words);
    }
}
