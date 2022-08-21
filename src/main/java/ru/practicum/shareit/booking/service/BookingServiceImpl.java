package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoAnswerFull;
import ru.practicum.shareit.booking.dto.BookingDtoAnswerPatch;
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
import java.util.Optional;
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
    public BookingDtoAnswer createBooking(Long userId, Booking booking) {
        User booker = userService.getUserById(userId);
        booking.setBooker(booker);
        Item item = itemService.getItemById(userId, booking.getItem().getId());
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Запрашиваемая вещь не доступна для бронирования");
        }
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
    public BookingDtoAnswerPatch updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (!optionalBooking.isPresent()) {
            throw new NotFoundException(String.format("Запрашиваемое бронирование с id = %d не найдено", bookingId));
        }
        Booking booking = optionalBooking.get();
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
        return bookingMapper.toBookingDtoAnswerPatch(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoAnswerFull getBookingById(Long userId, Long bookingId) {
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (!optionalBooking.isPresent()) {
            throw new NotFoundException(String.format("Запрашиваемое бронирование с id = %d не найдено", bookingId));
        }
        Booking booking = optionalBooking.get();
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException(String.format("Пользователь с id = %d не имеет доступа к проссмотру " +
                    "бронирования id = %d", userId, bookingId
            ));
        }
        return bookingMapper.toBookingDtoAnswerFull(booking);
    }

    /*  Получение списка всех бронирований текущего пользователя. Параметр state
необязательный и по умолчанию равен ALL (англ. «все»). Также он может принимать значения CURRENT (англ. «текущие»),
**PAST** (англ. «завершённые»), FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»),
REJECTED (англ. «отклонённые»). Бронирования должны возвращаться отсортированными по дате от более новых к более старым.*/
    @Override
    @Transactional
    public List<BookingDtoAnswerFull> getAllBookingsOfUser(Long userId, String state) {
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        List<Booking> bookings;
        final LocalDateTime currentTime = LocalDateTime.now();
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

    /*  Получение списка бронирований для всех вещей текущего пользователя.
    Этот запрос имеет смысл для владельца хотя бы одной вещи. Работа параметра state аналогична его работе в предыдущем сценарии.*/
    @Override
    @Transactional
    public List<BookingDtoAnswerFull> getAllBookingsOfItemsOwner(Long userId, String state) {
        List<Item> items = itemService.getAllItemsOfUser(userId);
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<Booking> bookings;
        final LocalDateTime currentTime = LocalDateTime.now();
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

    /*Метод для получения последнего и следующего бронирования вещи */
    @Override
    @Transactional
    public List<Booking> getLastAndNextBookingOfItem(Long item) {
        Booking last = bookingRepository.findByItemIdAndEndIsBeforeOrderByEnd(item, LocalDateTime.now());
        Booking next = bookingRepository.findByItemIdAndStartIsAfterOrderByStart(item, LocalDateTime.now());
        return List.of(last, next);
    }

}


