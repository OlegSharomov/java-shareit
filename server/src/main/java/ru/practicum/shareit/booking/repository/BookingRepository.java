package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Бронирования пользователя ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    Page<Booking> findAllByBookerId(Long userId, Pageable pageable);

    // Бронирования пользователя CURRENT
    List<Booking> findAllByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(Long bookerId, LocalDateTime end,
                                                                             LocalDateTime start);

    Page<Booking> findAllByBookerIdAndEndAfterAndStartBefore(Long bookerId, LocalDateTime end,
                                                             LocalDateTime start, Pageable pageable);

    // Бронирования пользователя PAST
    List<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(Long bookerId, BookingStatus status,
                                                                         LocalDateTime date);

    Page<Booking> findAllByBookerIdAndStatusAndEndBefore(Long bookerId, BookingStatus status,
                                                         LocalDateTime date, Pageable pageable);

    // Бронирования пользователя FUTURE
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime date);

    Page<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime date, Pageable pageable);

    // Бронирования пользователя WAITING и REJECTED
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    Page<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    // Бронирования вещей пользователя ALL
    List<Booking> findAllByItemInOrderByStartDesc(List<Item> items);

    Page<Booking> findAllByItemIn(List<Item> items, Pageable pageable);

    // Бронирования вещей пользователя CURRENT
    List<Booking> findAllByItemInAndEndAfterAndStartBeforeOrderByStartDesc(Collection<Item> item,
                                                                           LocalDateTime end, LocalDateTime start);

    Page<Booking> findAllByItemInAndEndAfterAndStartBefore(Collection<Item> item, LocalDateTime end,
                                                           LocalDateTime start, Pageable pageable);

    // Бронирования вещей пользователя PAST
    List<Booking> findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(Collection<Item> item, BookingStatus status,
                                                                       LocalDateTime date);

    Page<Booking> findAllByItemInAndStatusAndEndBefore(Collection<Item> item, BookingStatus status,
                                                       LocalDateTime date, Pageable pageable);

    // Бронирования вещей пользователя FUTURE
    List<Booking> findAllByItemInAndStartAfterOrderByStartDesc(Collection<Item> item, LocalDateTime date);

    Page<Booking> findAllByItemInAndStartAfter(Collection<Item> item, LocalDateTime date, Pageable pageable);

    // Бронирования вещей пользователя WAITING и REJECTED
    List<Booking> findAllByItemInAndStatusOrderByStartDesc(Collection<Item> item, BookingStatus status);

    Page<Booking> findAllByItemInAndStatus(Collection<Item> item, BookingStatus status, Pageable pageable);

    Booking findByItemIdAndEndIsBeforeOrderByEnd(Long itemId, LocalDateTime dateTime);

    Booking findByItemIdAndStartIsAfterOrderByStart(Long itemId, LocalDateTime dateTime);

    List<Booking> findAllByItemIdAndStatusAndEndBefore(Long itemId, BookingStatus status,
                                                       LocalDateTime dateTime);
}
