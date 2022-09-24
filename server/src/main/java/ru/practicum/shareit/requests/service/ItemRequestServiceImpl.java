package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoAnswer;
import ru.practicum.shareit.requests.dto.ItemRequestDtoAnswerFull;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestsRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = false)
    public ItemRequestDtoAnswer createItemRequest(ItemRequestDto itemRequestDto, Long userId, LocalDateTime createDate) {
        User requestor = userService.getEntityUserByIdFromStorage(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, requestor, createDate);
        itemRequestsRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDtoAnswer(itemRequest);
    }

    @Override
    @Transactional
    public List<ItemRequestDtoAnswerFull> getAllItemRequestsOfUser(Long userId) {
        User requestor = userService.getEntityUserByIdFromStorage(userId);
        List<ItemRequest> requests = itemRequestsRepository.getItemRequestsByRequestor(requestor);
        return requests.stream()
                .map(x -> {
                    List<ItemDtoAnswer> items = itemRepository.findAllByRequest(x).stream()
                            .map(e -> (itemMapper.toItemDtoAnswer(e, e.getRequest())))
                            .collect(Collectors.toList());
                    return itemRequestMapper.toItemRequestDtoAnswerFull(x,
                            items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemRequestDtoAnswerFull> getAllItemRequestsByParams(Long userId, Integer from, Integer size) {
        if (from == null || size == null) {
            return Collections.emptyList();
        }
        Pageable pag = PageRequest.of(from / size, size, Sort.by("created").descending());
        User requestor = userService.getEntityUserByIdFromStorage(userId);
        Page<ItemRequest> page = itemRequestsRepository.findAllByRequestorNot(requestor, pag);
        return page.stream()
                .map(x -> {
                    List<ItemDtoAnswer> items = itemRepository.findAllByRequest(x).stream()
                            .map(e -> itemMapper.toItemDtoAnswer(e, e.getRequest()))
                            .collect(Collectors.toList());
                    return itemRequestMapper.toItemRequestDtoAnswerFull(x, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemRequestDtoAnswerFull getItemRequestById(Long userId, Long itemRequestId) {
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException(String.format("Пользователь с переданным id = %d не найден", userId));
        }
        ItemRequest request = itemRequestsRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %d не найден", itemRequestId)));
        List<ItemDtoAnswer> items = itemRepository.findAllByRequest(request).stream()
                .map(e -> itemMapper.toItemDtoAnswer(e, e.getRequest()))
                .collect(Collectors.toList());
        return itemRequestMapper.toItemRequestDtoAnswerFull(request,
                items);
    }
}
