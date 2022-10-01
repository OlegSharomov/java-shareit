package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class BookingDtoAnswer {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long item;
}
