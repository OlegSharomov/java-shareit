package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoAnswerFull;

import java.util.List;

public interface BookingService {
    BookingDtoAnswer createBooking(Long userId, BookingDto bookingDto);

    BookingDtoAnswerFull updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    BookingDtoAnswerFull getBookingById(Long userId, Long bookingId);

    //  Getting a list of all bookings of the current user.
    List<BookingDtoAnswerFull> getAllBookingsOfUser(Long userId, SearchStatus state, Integer from, Integer size);

    // Getting a list of bookings for all the items of the current user.
    List<BookingDtoAnswerFull> getAllBookingsOfItemsOwner(Long userId, SearchStatus state, Integer from, Integer size);
}
