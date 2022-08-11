package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
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
    public Item getItemById(Long userId, Long itemId) {
        return itemRepository.getItemByIdFromStorage(userId, itemId);
    }

    @Override
    public List<Item> getAllItemsOfUser(Long userId) {
        return itemRepository.getAllItemsOfUserFromStorage(userId);
    }

    @Override
    public Item createItem(Long userId, Item item) {
//        if (!userRepository.isUserExistsById(userId)) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с переданным id не найден");
        }
        return itemRepository.createItemInStorage(userId, item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
//        if (!userRepository.isUserExistsById(userId)) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с переданным id не найден");
        }
        return itemRepository.updateItemInStorage(userId, itemId, item);
    }

    @Override
    public List<Item> searchForItemsByQueryText(String text) {
        if (text
//                .isBlank()
                .trim().isEmpty()
        ) {
            return Collections.emptyList();
        }
        String[] words = text.split(" ");
        return itemRepository.searchForItemsByQueryTextFromStorage(words);
    }
}