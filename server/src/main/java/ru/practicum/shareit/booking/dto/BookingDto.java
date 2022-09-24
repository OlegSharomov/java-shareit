package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    User booker;    // The user who created the booking
    BookingStatus status;
}
