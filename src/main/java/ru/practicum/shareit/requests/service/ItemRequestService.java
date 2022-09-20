package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoAnswer;
import ru.practicum.shareit.requests.dto.ItemRequestDtoAnswerFull;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoAnswer createItemRequest(ItemRequestDto itemRequestDto, Long userId, LocalDateTime createDate);

    List<ItemRequestDtoAnswerFull> getAllItemRequestsOfUser(Long userId);

    List<ItemRequestDtoAnswerFull> getAllItemRequestsByParams(Long userId, Integer from, Integer size);

    ItemRequestDtoAnswerFull getItemRequestById(Long userId, Long itemRequestId);
}
