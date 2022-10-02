package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Value
@Builder
public class ItemDtoAnswerFull {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoWithBookerId lastBooking;
    BookingDtoWithBookerId nextBooking;
    List<CommentDto> comments;
}