package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    Long id;
//    @NotNull
//    @Future(message = "Дата начала бронирования указана в прошлом")
    LocalDateTime start;
//    @NotNull
//    @Future(message = "Дата окончания бронирования указана в прошлом")
    LocalDateTime end;
    Long itemId;
    User booker;    // пользователь, который осуществляет бронирование
    BookingStatus status;
}
