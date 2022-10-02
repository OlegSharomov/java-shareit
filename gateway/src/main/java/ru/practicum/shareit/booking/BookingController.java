package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@NotNull(message = "The X-Sharer-User-Id is missing")
                                              @Positive(message = "'userId' must be positive")
                                              @RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero(message = "'from' must be positive or zero")
                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive(message = "'size' must be positive")
                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@NotNull(message = "The X-Sharer-User-Id is missing")
                                           @Positive(message = "'userId' must be positive")
                                           @RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        if (requestDto.getStart().isAfter(requestDto.getEnd())) {
            throw new ValidationException("Start cannot be after end");
        }
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeRequestStatus(@NotNull(message = "The X-Sharer-User-Id is missing")
                                                      @Positive(message = "'userId' must be positive")
                                                      @RequestHeader("X-Sharer-User-Id") long userId,
                                                      @Positive(message = "'bookingId' must be positive")
                                                      @PathVariable("bookingId") Long bookingId,
                                                      @NotNull(message = "'approved' must be specified")
                                                      @RequestParam(name = "approved") Boolean approved) {
        log.info("Received a request: PATCH/bookings/{}?approved={{}} from user id = {} ",
                bookingId, approved, userId);
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@NotNull(message = "The X-Sharer-User-Id is missing")
                                             @Positive(message = "'userId' must be positive")
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @Positive(message = "'bookingId' must be positive")
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object>
    getAllBookingsOfItemsOwner(@NotNull(message = "The X-Sharer-User-Id is missing")
                               @Positive(message = "'userId' must be positive")
                               @RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestParam(name = "state", required = false,
                                       defaultValue = "ALL") String stateParam,
                               @PositiveOrZero(message = "'from' must be positive or zero")
                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                               @Positive(message = "Значение size должно быть позитивным")
                               @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Received a request: GET/bookings/owner?state={}&from={}&size={} from user id = {}",
                stateParam, from, size, userId);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllBookingsOfItemsOwner(userId, state, from, size);
    }
}
