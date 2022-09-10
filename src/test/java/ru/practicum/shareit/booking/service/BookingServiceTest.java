package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoAnswerFull;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemServiceImpl itemService;
    @Spy
    private BookingMapperImpl bookingMapper;

    static LocalDateTime minuteOfToday = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(), LocalDateTime.MIN.getHour(), LocalDateTime.now().getMinute());
    User user1 = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
    User user2 = User.builder().id(2L).name("user2").email("user2@mail.ru").build();
    User user3 = User.builder().id(3L).name("user3").email("user3@mail.ru").build();
    Item item1 = Item.builder().id(1L).name("item1").description("description item1").available(true).owner(user3).build();
    BookingDto bookingDto1 = BookingDto.builder().id(1L).start(minuteOfToday.plusDays(1))
            .end(minuteOfToday.minusDays(2)).build();
    Booking booking1 = Booking.builder().id(1L).start(minuteOfToday.plusDays(1))
            .end(minuteOfToday.minusDays(2)).item(item1).booker(user1).status(WAITING).build();


    // createBooking
    @Test
    public void shouldThrowExceptionWhenWeTryCreateBookingAndBookingAlreadyExists() {
        when(bookingRepository.existsById(1L)).thenReturn(true);
        RuntimeException re = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(1L, bookingDto1));
        Assertions.assertEquals(re.getMessage(), "Данные бронирования можно изменять только через метод PATCH");
    }

    @Test
    public void shouldThrowExceptionWhenWeTryCreateBookingAndStartOfBookingBeforeNow() {
        BookingDto bookingDto1 = BookingDto.builder().itemId(1L).start(minuteOfToday.minusDays(1))
                .end(minuteOfToday.minusDays(2)).build();
        RuntimeException re = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(1L, bookingDto1));
        Assertions.assertEquals(re.getMessage(),
                "Дата начала бронирования должна быть раньше даты окончания бронирования");
    }

    @Test
    public void shouldThrowExceptionWhenBookerAndOwnerSame() {
        BookingDto bookingDto1 = BookingDto.builder().itemId(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.plusDays(2)).build();
        when(itemService.getEntityItemByIdFromStorage(1L)).thenReturn(item1);
        RuntimeException re = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(3L, bookingDto1));
        Assertions.assertEquals(re.getMessage(), "Вещи не доступны для бронирования их владельцам");
    }

    @Test
    public void shouldThrowExceptionWhenItemIsUnavailable() {
        Item itemUnavailable = Item.builder()
                .id(1L).name("item1").description("description item1").available(false).owner(user3).build();
        BookingDto bookingDto1 = BookingDto.builder().itemId(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.plusDays(2)).build();
        when(itemService.getEntityItemByIdFromStorage(1L)).thenReturn(itemUnavailable);
        RuntimeException re = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(1L, bookingDto1));
        Assertions.assertEquals(re.getMessage(), "Запрашиваемая вещь не доступна для бронирования");
    }

    @Test
    public void shouldReturnBookingDtoWhenWeCreateBooking() {
        BookingDto bookingDto1 = BookingDto.builder().itemId(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.plusDays(2)).build();
        when(itemService.getEntityItemByIdFromStorage(1L)).thenReturn(item1);
        when(userService.getEntityUserByIdFromStorage(1L)).thenReturn(user1);
        Booking bookingForRepository = Booking.builder().start(minuteOfToday.plusDays(1)).end(minuteOfToday.plusDays(2))
                .item(item1).booker(user1).status(WAITING).build();
        Booking bookingFromRepository = Booking.builder().id(1L).start(minuteOfToday.plusDays(1)).end(minuteOfToday.plusDays(2))
                .item(item1).booker(user1).status(WAITING).build();
        when(bookingRepository.save(bookingForRepository)).thenReturn(bookingFromRepository);
        BookingDtoAnswer result = bookingService.createBooking(1L, bookingDto1);
        BookingDtoAnswer bookingToCheck = BookingDtoAnswer.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.plusDays(2)).item(1L).build();
        Assertions.assertEquals(bookingToCheck, result);
    }

    // updateBookingStatus
    @Test
    public void shouldThrowExceptionWhenBookingNotExists() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException re = assertThrows(NotFoundException.class,
                () -> bookingService.updateBookingStatus(1L, 1L, false));
        assertEquals("Запрашиваемое бронирование с id = 1 не найдено", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenUserIsNonBooker() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        RuntimeException re = assertThrows(NotFoundException.class,
                () -> bookingService.updateBookingStatus(1L, 1L, false));
        assertEquals("Пользователь с id = 1 не может редактировать статус " +
                "бронирования для вещи id = 1, т.к. он не является ее владельцем", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenStatusBookingIsAlreadyApproved() {
        Booking bookingApproved = Booking.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.minusDays(2)).item(item1).booker(user1).status(APPROVED).build();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingApproved));
        RuntimeException re = assertThrows(ValidationException.class,
                () -> bookingService.updateBookingStatus(3L, 1L, false));
        assertEquals("Статус бронирования уже подтвержден", re.getMessage());
    }

    @Test
    public void shouldReturnBookingDtoWithStatusRejectedWhenWeUpdateStatusWithFalse() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        Booking bookingToStorage = Booking.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.minusDays(2)).item(item1).booker(user1).status(REJECTED).build();
        when(bookingRepository.save(bookingToStorage)).thenReturn(bookingToStorage);
        BookingDtoAnswerFull bookingDtoToCheck = BookingDtoAnswerFull.builder()
                .id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.minusDays(2)).status(REJECTED).booker(user1).item(item1).build();
        BookingDtoAnswerFull result = bookingService.updateBookingStatus(3L, 1L, false);
        assertEquals(bookingDtoToCheck, result);
    }

    @Test
    public void shouldReturnBookingDtoWithStatusApprovedWhenWeUpdateStatusWithTrue() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        Booking bookingToStorage = Booking.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.minusDays(2)).item(item1).booker(user1).status(APPROVED).build();
        when(bookingRepository.save(bookingToStorage)).thenReturn(bookingToStorage);
        BookingDtoAnswerFull bookingDtoToCheck = BookingDtoAnswerFull.builder()
                .id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.minusDays(2)).status(APPROVED).booker(user1).item(item1).build();
        BookingDtoAnswerFull result = bookingService.updateBookingStatus(3L, 1L, true);
        assertEquals(bookingDtoToCheck, result);
    }

    // getBookingById
    @Test
    public void shouldThrowExceptionWhenUserNotExists() {
        when(userService.isUserExists(999L)).thenReturn(false);
        RuntimeException re = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(999L, 1L));
        assertEquals("Пользователь с id = 999 не найден", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenBookingNotFound() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException re = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1L, 999L));
        assertEquals("Запрашиваемое бронирование с id = 999 не найдено", re.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenUserIsNotBookerOrOwner() {
        when(userService.isUserExists(2L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        RuntimeException re = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(2L, 1L));
        assertEquals("Пользователь с id = 2 не имеет доступа к проссмотру " +
                "бронирования id = 1", re.getMessage());
    }

    @Test
    public void shouldReturnBookingDtoWhenUserIsBooker() {
        when(userService.isUserExists(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        BookingDtoAnswerFull bookingToCheck = BookingDtoAnswerFull.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.minusDays(2)).item(item1).booker(user1).status(WAITING).build();
        BookingDtoAnswerFull result = bookingService.getBookingById(1L, 1L);
        assertEquals(bookingToCheck, result);
    }

    @Test
    public void shouldReturnBookingDtoWhenUserIsOwner() {
        when(userService.isUserExists(3L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        BookingDtoAnswerFull bookingToCheck = BookingDtoAnswerFull.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.minusDays(2)).item(item1).booker(user1).status(WAITING).build();
        BookingDtoAnswerFull result = bookingService.getBookingById(3L, 1L);
        assertEquals(bookingToCheck, result);
    }

    // getAllBookingsOfUser
    @Test
    public void should(){
        when(userService.isUserExists(999L)).thenReturn(false);
        RuntimeException re = assertThrows(NotFoundException.class, () -> bookingService
                .getAllBookingsOfUser(999L, "ALL", 0, 20));
        assertEquals("Пользователь с id = 999 не найден", re.getMessage());
    }
}
