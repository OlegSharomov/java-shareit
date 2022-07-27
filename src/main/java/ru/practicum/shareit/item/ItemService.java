package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoController;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

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

    public ItemDtoController getItemById(Long userId, Long itemId) {
        return itemRepository.getItemByIdFromStorage(userId, itemId);
    }

    public List<Item> getAllItems() {
        return itemRepository.getAllItemsFromStorage();
    }

    public ItemDtoService createItem(Long userId, ItemDtoService itemDtoService) {
        if (!userRepository.userIsContainsById(userId)) {
            throw new UserNotFoundException("Пользователь с переданным id не найден");
        }
        return itemRepository.createItemInStorage(userId, itemDtoService);
    }

    public ItemDtoController updateItem(Long userId, Long itemId, ItemDtoController itemDto) {
        return itemRepository.updateItemInStorage(userId, itemId, itemDto);
    }

    public List<ItemDtoController> findItems(String text) {
        return itemRepository.findItemsFromStorage(text);
    }

}
