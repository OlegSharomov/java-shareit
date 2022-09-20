package ru.practicum.shareit.item.dto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "description", source = "itemDto.description")
    @Mapping(target = "request", source = "itemRequest")
    Item toItem(ItemDto itemDto, ItemRequest itemRequest);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "requestId", source = "request.id")
    ItemDtoAnswer toItemDtoAnswer(Item item, ItemRequest request);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "last", target = "lastBooking")
    @Mapping(source = "next", target = "nextBooking")
    @Mapping(source = "comments", target = "comments")
    ItemDtoAnswerFull toItemDtoAnswerFull(Item item, BookingDtoWithBookerId last, BookingDtoWithBookerId next,
                                          List<CommentDto> comments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "dto.id", target = "id")
    @Mapping(source = "dto.description", target = "description")
    @Mapping(source = "itemRequest", target = "request")
    void updateItemFromDto(ItemDto dto, @MappingTarget Item item, ItemRequest itemRequest);
}
