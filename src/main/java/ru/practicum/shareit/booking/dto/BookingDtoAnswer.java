package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoAnswer {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long item;
}
