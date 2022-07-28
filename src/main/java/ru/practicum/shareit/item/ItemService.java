package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoController;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;

/*Для каждого из данных сценариев создайте соответственный метод в контроллере.
Также создайте интерфейс ItemService и реализующий его класс ItemServiceImpl, к которому будет обращаться ваш контроллер.
В качестве DAO создайте реализации, которые будут хранить данные в памяти приложения.
Работу с базой данных вы реализуете в следующем спринте.*/
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDtoService getItemById(Long userId, Long itemId) {
        return itemRepository.getItemByIdFromStorage(userId, itemId);
    }

    public List<ItemDtoService> getAllItems(Long userId) {
        return itemRepository.getAllItemsFromStorage(userId);
    }

    public ItemDtoService createItem(Long userId, ItemDtoService itemDtoService) {
        if (!userRepository.userIsContainsById(userId)) {
            throw new UserNotFoundException("Пользователь с переданным id не найден");
        }
        return itemRepository.createItemInStorage(userId, itemDtoService);
    }

    public ItemDtoService updateItem(Long userId, Long itemId, ItemDtoService itemDtoService) {
        if (!userRepository.userIsContainsById(userId)) {
            throw new UserNotFoundException("Пользователь с переданным id не найден");
        }
        return itemRepository.updateItemInStorage(userId, itemId, itemDtoService);
    }

    public List<ItemDtoService> findItems(String text) {
        if(text.isBlank()){return Collections.emptyList();}
        String[] words = text.split(" ");
        return itemRepository.findItemsFromStorage(words);
    }

}
