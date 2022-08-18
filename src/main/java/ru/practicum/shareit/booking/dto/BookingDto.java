package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class BookingDto {
    Long id;
    @NotNull
    @Future(message = "дата начала бронирования указана в прошлом")
    LocalDateTime start;
    @NotNull
    @Future(message = "дата окончания бронирования указана в прошлом")
    LocalDateTime end;
    Long itemId;
    User booker;    // пользователь, который осуществляет бронирование
    BookingStatus status;
}
