package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoAnswerFull;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.SearchStatus;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /*  Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем,
    а затем подтверждён владельцем вещи. После создания запрос находится в статусе WAITING — «ожидает подтверждения». */
    @PostMapping
    public BookingDtoAnswer createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody BookingDto bookingDto) {
        log.info("Получен запрос POST/bookings от пользователя id = {} с переданным телом: {}", userId, bookingDto);
        return bookingService.createBooking(userId, bookingDto);
    }

    /* Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    Затем статус бронирования становится либо APPROVED, либо REJECTED.*/
    @PatchMapping("/{bookingId}")
    public BookingDtoAnswerFull changeRequestStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable("bookingId") Long bookingId,
                                                    @RequestParam("approved") Boolean approved) {
        log.info("Получен запрос PATCH/bookings/{bookingId}?approved={approved} от пользователя id = {} " +
                "на подтверждение бронирования: {}, со значением {}", userId, bookingId, approved);
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    /*  Получение данных о конкретном бронировании (включая его статус).
    Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование.*/
    @GetMapping("/{bookingId}")
    public BookingDtoAnswerFull getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable("bookingId") Long bookingId) {
        log.info("Получен запрос GET/bookings/{bookingId} от пользователя id = {} " +
                "на просмотр бронирования id = {}", userId, bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }


    /*  Получение списка всех бронирований текущего пользователя. Параметр state необязательный и по умолчанию
    равен ALL (англ. «все»). Также он может принимать значения CURRENT (англ. «текущие»), PAST (англ. «завершённые»),
    FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
    Бронирования должны возвращаться отсортированными по дате от более новых к более старым.*/
    @GetMapping
    public List<BookingDtoAnswerFull>
    getAllBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                         @RequestParam(required = false, defaultValue = "ALL") SearchStatus state,
                         @RequestParam(required = false) Integer from,
                         @RequestParam(required = false) Integer size) {
        log.info("Получен запрос GET/bookings?state={state} от пользователя id = {} " +
                "на просмотр бронирований со статусом: {}, страницы: от{} до {}", userId, state, from, size);
        return bookingService.getAllBookingsOfUser(userId, state, from, size);
    }


    /* GET /bookings/owner?state={state}   Получение списка бронирований для всех вещей текущего пользователя.*/
    @GetMapping("/owner")
    public List<BookingDtoAnswerFull>
    getAllBookingsOfItemsOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestParam(required = false, defaultValue = "ALL") SearchStatus state,
                               @RequestParam(required = false) Integer from,
                               @RequestParam(required = false) Integer size) {
        log.info("Получен запрос GET/bookings/owner?state={state} от пользователя id = {} " +
                "на просмотр бронирований со статусом: {}, страницы: от {} до {}", userId, state, from, size);
        return bookingService.getAllBookingsOfItemsOwner(userId, state, from, size);
    }
}
