package ru.practicum.shareit.item.dto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toItem(ItemDto itemDto);

    ItemDtoAnswer toItemDtoAnswer(Item item);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "last", target = "lastBooking")
    @Mapping(source = "next", target = "nextBooking")
    @Mapping(source = "comments", target = "comments")
    ItemDtoAnswerFull toItemDtoAnswerFull(Item item, BookingDtoWithBookerId last, BookingDtoWithBookerId next,
                                          List<CommentDto> comments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(ItemDto dto, @MappingTarget Item item);
}
