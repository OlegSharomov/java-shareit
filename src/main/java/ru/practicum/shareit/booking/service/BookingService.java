package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoAnswerFull;

import java.util.List;

public interface BookingService {
    BookingDtoAnswer createBooking(Long userId, BookingDto bookingDto);

    BookingDtoAnswerFull updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    BookingDtoAnswerFull getBookingById(Long userId, Long bookingId);

    //  Получение списка всех бронирований текущего пользователя.
    List<BookingDtoAnswerFull> getAllBookingsOfUser(Long userId, String state, Integer from, Integer size);

    // Получение списка бронирований для всех вещей текущего пользователя.
    List<BookingDtoAnswerFull> getAllBookingsOfItemsOwner(Long userId, String state, Integer from, Integer size);
}
