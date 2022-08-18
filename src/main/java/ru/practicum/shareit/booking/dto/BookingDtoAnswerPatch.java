package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class BookingDtoAnswerPatch {
    Long id;
    BookingStatus status;
    User booker;
    Item item;
}
