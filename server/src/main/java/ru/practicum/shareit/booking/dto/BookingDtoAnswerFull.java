package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoAnswerFull {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    User booker;
    Item item;
}
