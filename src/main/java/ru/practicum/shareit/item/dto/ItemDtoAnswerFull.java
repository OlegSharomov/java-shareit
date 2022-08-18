package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;

@Value
@Builder
public class ItemDtoAnswerFull {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoWithBookerId lastBooking;
    BookingDtoWithBookerId nextBooking;
}