package ru.practicum.shareit.requests.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDtoAnswer;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    @Mapping(target = "id", source = "itemRequestDto.id")
    @Mapping(target = "created", source = "created")
    @Mapping(target = "requestor", source = "requestor")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor, LocalDateTime created);

    ItemRequestDtoAnswer toItemRequestDtoAnswer(ItemRequest itemRequest);

    @Mapping(target = "id", source = "itemRequest.id")
    @Mapping(target = "items", source = "items")
    ItemRequestDtoAnswerFull toItemRequestDtoAnswerFull(ItemRequest itemRequest,
                                                        List<ItemDtoAnswer> items);
}
