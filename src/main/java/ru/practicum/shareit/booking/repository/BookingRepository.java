package ru.practicum.shareit.booking.repository;

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
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long booker_id, BookingStatus status);

    List<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(Long booker_id, BookingStatus status,
                                                                         LocalDateTime date);

    List<Booking> findAllByBookerIdAndEndAfterOrderByStartDesc(Long booker_id, LocalDateTime date);

    List<Booking> findAllByItemInOrderByStartDesc(List<Item> items);

    List<Booking> findAllByItemInAndStatusOrderByStartDesc(Collection<Item> item, BookingStatus status);

    List<Booking> findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(Collection<Item> item, BookingStatus status,
                                                                       LocalDateTime date);

    List<Booking> findAllByItemInAndEndAfterOrderByStartDesc(Collection<Item> item, LocalDateTime date);

    Booking findByItemIdAndEndIsBeforeOrderByEnd(Long itemId, LocalDateTime dateTime);

    Booking findByItemIdAndStartIsAfterOrderByStart(Long itemId, LocalDateTime dateTime);
}
