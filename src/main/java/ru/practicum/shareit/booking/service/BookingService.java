package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoAnswerFull;
import ru.practicum.shareit.booking.dto.BookingDtoAnswerPatch;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    @Transactional(readOnly = false)
    BookingDtoAnswer createBooking(Long userId, Booking booking);

    @Transactional(readOnly = false)
    BookingDtoAnswerPatch updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    @Transactional
    BookingDtoAnswerFull getBookingById(Long userId, Long bookingId);

    /*  Получение списка всех бронирований текущего пользователя. Параметр state
    необязательный и по умолчанию равен ALL (англ. «все»). Также он может принимать значения CURRENT (англ. «текущие»),
    **PAST** (англ. «завершённые»), FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»),
    REJECTED (англ. «отклонённые»). Бронирования должны возвращаться отсортированными по дате от более новых к более старым.*/
    @Transactional
    List<BookingDtoAnswerFull> getAllBookingsOfUser(Long userId, String state);

    /*  Получение списка бронирований для всех вещей текущего пользователя.
        Этот запрос имеет смысл для владельца хотя бы одной вещи. Работа параметра state аналогична его работе в предыдущем сценарии.*/
    @Transactional
    List<BookingDtoAnswerFull> getAllBookingsOfItemsOwner(Long userId, String state);

    /*Метод для получения последнего и следующего бронирования вещи */
    @Transactional
    List<Booking> getLastAndNextBookingOfItem(Long item);
}
