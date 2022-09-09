package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoAnswerFull;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = false)
    public BookingDtoAnswer createBooking(Long userId, BookingDto bookingDto) {
        if (bookingDto.getId() != null && isBookingExists(bookingDto.getId())) {
            throw new ValidationException("Данные бронирования можно изменять только через метод PATCH");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования должна быть раньше даты окончания бронирования");
        }
        Booking booking = bookingMapper.toBooking(bookingDto);
        Item item = itemService.getEntityItemByIdFromStorage(booking.getItem().getId());
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Запрашиваемая вещь не доступна для бронирования");
        }
        User booker = userService.getEntityUserByIdFromStorage(userId);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(WAITING);
        if (!item.getAvailable()) {
            throw new ValidationException("Запрашиваемая вещь не доступна для бронирования");
        }
        Booking readyBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDtoAnswer(readyBooking);
    }

    @Override
    @Transactional(readOnly = false)
    public BookingDtoAnswerFull updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(String
                .format("Запрашиваемое бронирование с id = %d не найдено", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не может редактировать статус " +
                    "бронирования для вещи id = %d, т.к. он не является ее хозяином", userId, booking.getItem().getId()));
        }
        if (booking.getStatus().equals(APPROVED)) {
            throw new ValidationException("Статус бронирования уже подтвержден");
        }
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        return bookingMapper.toBookingDtoAnswerFull(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoAnswerFull getBookingById(Long userId, Long bookingId) {
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(String
                .format("Запрашиваемое бронирование с id = %d не найдено", bookingId)));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException(String.format("Пользователь с id = %d не имеет доступа к проссмотру " +
                    "бронирования id = %d", userId, bookingId
            ));
        }
        return bookingMapper.toBookingDtoAnswerFull(booking);
    }

    @Override
    @Transactional
    public List<BookingDtoAnswerFull> getAllBookingsOfUser(Long userId, String state, Integer from, Integer size) {
        final LocalDateTime currentTime = LocalDateTime.now();
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        if (from == null || size == null) {
            return collectAllBookingsOfUser(userId, state, currentTime);
        } else {
            return collectAllBookingsOfUserWithPages(userId, state, currentTime, from, size);
        }
    }

    private List<BookingDtoAnswerFull> collectAllBookingsOfUser(Long userId, String state, LocalDateTime currentTime) {
        List<Booking> bookings;
        switch (state.trim().toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(userId,
                        currentTime, currentTime);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(userId,
                        APPROVED, currentTime);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                        currentTime);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, REJECTED);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(bookingMapper::toBookingDtoAnswerFull).collect(Collectors.toList());
    }

    private List<BookingDtoAnswerFull> collectAllBookingsOfUserWithPages(Long userId, String state,
                                                                         LocalDateTime currentTime, Integer from,
                                                                         Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        Page<Booking> pages;
        switch (state.trim().toUpperCase()) {
            case "ALL":
                pages = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case "CURRENT":
                pages = bookingRepository.findAllByBookerIdAndEndAfterAndStartBefore(userId,
                        currentTime, currentTime, pageable);
                break;
            case "PAST":
                pages = bookingRepository.findAllByBookerIdAndStatusAndEndBefore(userId,
                        APPROVED, currentTime, pageable);
                break;
            case "FUTURE":
                pages = bookingRepository.findAllByBookerIdAndStartAfter(userId, currentTime, pageable);
                break;
            case "WAITING":
                pages = bookingRepository.findAllByBookerIdAndStatus(userId, WAITING, pageable);
                break;
            case "REJECTED":
                pages = bookingRepository.findAllByBookerIdAndStatus(userId, REJECTED, pageable);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return pages.stream()
                .map(bookingMapper::toBookingDtoAnswerFull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingDtoAnswerFull> getAllBookingsOfItemsOwner(Long userId, String state, Integer from, Integer size) {
        // проверить существование пользователя
        final LocalDateTime currentTime = LocalDateTime.now();
        List<Item> items = itemService.getAllEntityItemsOfUserFromStorage(userId);
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        if (from == null || size == null) {
            return collectAllBookingsOfItemsOwner(state, items, currentTime);
        } else {
            return collectAllBookingsOfItemsOwnerWithPages(state, items, currentTime, from, size);
        }
    }

    private List<BookingDtoAnswerFull> collectAllBookingsOfItemsOwner(String state, List<Item> items,
                                                                      LocalDateTime currentTime) {
        List<Booking> bookings;
        switch (state.trim().toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findAllByItemInOrderByStartDesc(items);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemInAndEndAfterAndStartBeforeOrderByStartDesc(items,
                        currentTime, currentTime);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(items,
                        APPROVED, currentTime);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemInAndStartAfterOrderByStartDesc(items,
                        currentTime);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemInAndStatusOrderByStartDesc(items, WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemInAndStatusOrderByStartDesc(items, REJECTED);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(bookingMapper::toBookingDtoAnswerFull).collect(Collectors.toList());
    }

    private List<BookingDtoAnswerFull> collectAllBookingsOfItemsOwnerWithPages(String state, List<Item> items,
                                                                               LocalDateTime currentTime, Integer from,
                                                                               Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        Page<Booking> pages;
        switch (state.trim().toUpperCase()) {
            case "ALL":
                pages = bookingRepository.findAllByItemIn(items, pageable);
                break;
            case "CURRENT":
                pages = bookingRepository.findAllByItemInAndEndAfterAndStartBefore(items, currentTime,
                        currentTime, pageable);
                break;
            case "PAST":
                pages = bookingRepository.findAllByItemInAndStatusAndEndBefore(items, APPROVED,
                        currentTime, pageable);
                break;
            case "FUTURE":
                pages = bookingRepository.findAllByItemInAndStartAfter(items, currentTime, pageable);
                break;
            case "WAITING":
                pages = bookingRepository.findAllByItemInAndStatus(items, WAITING, pageable);
                break;
            case "REJECTED":
                pages = bookingRepository.findAllByItemInAndStatus(items, REJECTED, pageable);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return pages.stream()
                .map(bookingMapper::toBookingDtoAnswerFull)
                .collect(Collectors.toList());
    }

    public boolean isBookingExists(Long bookingId) {
        return bookingRepository.existsById(bookingId);
    }
}


