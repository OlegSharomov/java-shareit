package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {
    @InjectMocks
    BookingMapperImpl bookingMapper;
    LocalDateTime minuteOfToday = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(), LocalDateTime.MIN.getHour(), LocalDateTime.now().getMinute());
    User user1 = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
    User user2 = User.builder().id(2L).name("user2").email("user2@mail.ru").build();
    Item item1 = Item.builder().id(1L).name("item1").description("description item1").available(true)
            .owner(user2).build();
    Booking booking1 = Booking.builder().id(1L).start(minuteOfToday.plusDays(1))
            .end(minuteOfToday.plusDays(2)).item(item1).booker(user1).status(APPROVED).build();

    // toBooking
    @Test
    public void shouldReturnBooking() {
        Item itemToCheck = Item.builder().id(1L).build();
        BookingDto bookingDto = BookingDto.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.plusDays(2)).itemId(1L).booker(user1).status(APPROVED).build();
        Booking bookingToCheck = Booking.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.plusDays(2)).item(itemToCheck).booker(user1).status(APPROVED).build();
        Booking result = bookingMapper.toBooking(bookingDto);
        assertEquals(bookingToCheck, result);
    }

    // toBookingDtoAnswer
    @Test
    public void shouldReturnBookingDtoAnswer() {
        BookingDtoAnswer bookingDtoToCheck = BookingDtoAnswer.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.plusDays(2)).item(1L).build();
        BookingDtoAnswer result = bookingMapper.toBookingDtoAnswer(booking1);
        assertEquals(bookingDtoToCheck, result);
    }

    // toBookingDtoAnswerFull
    @Test
    public void shouldReturnBookingDtoAnswerFull() {
        BookingDtoAnswerFull bookingDtoToCheck = BookingDtoAnswerFull.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.plusDays(2)).status(APPROVED).booker(user1).item(item1).build();
        BookingDtoAnswerFull result = bookingMapper.toBookingDtoAnswerFull(booking1);
        assertEquals(bookingDtoToCheck, result);
    }

    // toBookingDtoWithBookerId
    @Test
    public void should() {
        BookingDtoWithBookerId bookingDtoToCheck = BookingDtoWithBookerId.builder().id(1L).bookerId(1L).build();
        BookingDtoWithBookerId result = bookingMapper.toBookingDtoWithBookerId(booking1);
        assertEquals(bookingDtoToCheck, result);
    }

}
