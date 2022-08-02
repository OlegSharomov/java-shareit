package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class Booking {
    @Positive
    private final Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;    // пользователь, который осуществляет бронирование
    private BookingStatus status;   /* - статус бронирования (ожидает одобрения, подтверждено владельцем,
                                     отклонено владельцем или отменено создателем) */
}