package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoAnswerFull;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.SearchStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    LocalDateTime minuteOfToday = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(), LocalDateTime.MIN.getHour(), LocalDateTime.now().getMinute(),
            LocalDateTime.now().getSecond());
    User user1 = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
    User user2 = User.builder().id(2L).name("user2").email("user2@mail.ru").build();
    Item item1 = Item.builder().id(1L).name("item1").description("description item1").available(true).owner(user2).build();

    BookingDto bookingDto1 = BookingDto.builder().id(1L).start(minuteOfToday.plusDays(1))
            .end(minuteOfToday.plusDays(2)).build();
    BookingDtoAnswer dtoAnswer1 = BookingDtoAnswer.builder().id(1L).start(minuteOfToday.plusDays(1))
            .end(minuteOfToday.plusDays(2)).item(1L).build();
    BookingDtoAnswerFull dtoAnswerFull1 = BookingDtoAnswerFull.builder().id(1L).start(minuteOfToday.plusDays(2))
            .end(minuteOfToday.plusDays(3)).item(item1).booker(user1).status(REJECTED).build();
    BookingDtoAnswerFull dtoAnswerFull2 = BookingDtoAnswerFull.builder().id(2L).start(minuteOfToday.plusDays(3))
            .end(minuteOfToday.plusDays(4)).item(item1).booker(user1).status(APPROVED).build();

    // createBooking
    @Test
    public void shouldCallCreateBookingAndReturnBookingDtoAnswer() throws Exception {
        when(bookingService.createBooking(eq(1L), any(BookingDto.class))).thenReturn(dtoAnswer1);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dtoAnswer1.getId()))
                .andExpect(jsonPath("$.start").value(dtoAnswer1.getStart().toString()))
                .andExpect(jsonPath("$.end").value(dtoAnswer1.getEnd().toString()))
                .andExpect(jsonPath("$.item").value(1L));
        Mockito.verify(bookingService, Mockito.times(1))
                .createBooking(eq(1L), any(BookingDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenStartIsPast() throws Exception {
        BookingDto bookingDtoPast = BookingDto.builder().id(1L).start(minuteOfToday.minusDays(1))
                .end(minuteOfToday.plusDays(2)).build();
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(bookingDtoPast))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Дата начала бронирования указана в прошлом")));
        Mockito.verify(bookingService, Mockito.times(0))
                .createBooking(any(Long.class), any(BookingDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenEndIsPast() throws Exception {
        BookingDto bookingDtoPast = BookingDto.builder().id(1L).start(minuteOfToday.plusDays(1))
                .end(minuteOfToday.minusDays(2)).build();
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(bookingDtoPast))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Дата окончания бронирования указана в прошлом")));
        Mockito.verify(bookingService, Mockito.times(0))
                .createBooking(any(Long.class), any(BookingDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenBodyNotExists() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));
        Mockito.verify(bookingService, Mockito.times(0))
                .createBooking(any(Long.class), any(BookingDto.class));
    }

    // changeRequestStatus
    @Test
    public void shouldCallBookingServiceUpdateAnrReturnBookingDtoAnswerFull() throws Exception {
        when(bookingService.updateBookingStatus(1L, 1L, false)).thenReturn(dtoAnswerFull1);
        mockMvc.perform(patch("/bookings/{bookingId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$.start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$.end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$.item.name").value(item1.getName()))
                .andExpect(jsonPath("$.booker.name").value(user1.getName()))
                .andExpect(jsonPath("$.status").value(dtoAnswerFull1.getStatus().toString()));
        Mockito.verify(bookingService, Mockito.times(1))
                .updateBookingStatus(1L, 1L, false);
    }

    @Test
    public void shouldThrowExceptionWhenRequestParamNotExists() throws Exception {
        when(bookingService.updateBookingStatus(1L, 1L, false)).thenReturn(dtoAnswerFull1);
        mockMvc.perform(patch("/bookings/{bookingId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MissingServletRequestParameterException));
        Mockito.verify(bookingService, Mockito.times(0))
                .updateBookingStatus(1L, 1L, false);
    }

    // getBooking
    @Test
    public void shouldReturnBookingDtoAnswerFull() throws Exception {
        Mockito.when(bookingService.getBookingById(1L, 1L)).thenReturn(dtoAnswerFull1);
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldThrowExceptionWhenBookingIdNegative() throws Exception {
        Mockito.when(bookingService.getBookingById(1L, 1L)).thenReturn(dtoAnswerFull1);
        mockMvc.perform(get("/bookings/{bookingId}", "-1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Переменная bookingId должна быть указана и быть больше 0")));
    }

    // getAllBookingsOfUser
    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenAllRequestParametersPresent() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfUser(1L, SearchStatus.ALL, 0, 5)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParametersOnlyState() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfUser(1L, SearchStatus.ALL, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParametersNotExists() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfUser(1L, SearchStatus.ALL, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParameterIsCurrent() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfUser(1L, SearchStatus.CURRENT, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "CURRENT")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParameterIsPast() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfUser(1L, SearchStatus.PAST, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "PAST")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParameterIsFuture() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfUser(1L, SearchStatus.FUTURE, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParameterIsWaiting() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfUser(1L, SearchStatus.WAITING, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParameterIsRejected() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfUser(1L, SearchStatus.REJECTED, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "REJECTED")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldThrowExceptionWhenWhenRequestParameterIsUNKNOWN_STATE() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "UNSUPPORTED_STATUS")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("Unknown state: UNSUPPORTED_STATUS")));
    }

    // getAllBookingsOfItemsOwner
    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenAllRequestParametersPresent() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfItemsOwner(1L, SearchStatus.ALL, 0, 5)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParametersOnlyState() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfItemsOwner(1L, SearchStatus.ALL, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParametersNotExists() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfItemsOwner(1L, SearchStatus.ALL, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParameterIsCurrent() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfItemsOwner(1L, SearchStatus.CURRENT, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "CURRENT")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParameterIsPast() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfItemsOwner(1L, SearchStatus.PAST, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "PAST")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParameterIsFuture() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfItemsOwner(1L, SearchStatus.FUTURE, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParameterIsWaiting() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfItemsOwner(1L, SearchStatus.WAITING, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParameterIsRejected() throws Exception {
        List<BookingDtoAnswerFull> listAnswer = List.of(dtoAnswerFull1, dtoAnswerFull2);
        when(bookingService.getAllBookingsOfItemsOwner(1L, SearchStatus.REJECTED, null, null)).thenReturn(listAnswer);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "REJECTED")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dtoAnswerFull1.getId()))
                .andExpect(jsonPath("$[0].start").value(dtoAnswerFull1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(dtoAnswerFull1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.name").value(item1.getName()))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].status").value(dtoAnswerFull1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(dtoAnswerFull2.getId()))
                .andExpect(jsonPath("$[1].start").value(dtoAnswerFull2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(dtoAnswerFull2.getEnd().toString()))
                .andExpect(jsonPath("$[1].status").value(dtoAnswerFull2.getStatus().toString()));
    }

    @Test
    public void shouldThrowExceptionWhenWhenInMethodgetAllBookingsOfItemsOwnerRequestParameterIsUNKNOWN_STATE() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "UNSUPPORTED_STATUS")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("Unknown state: UNSUPPORTED_STATUS")));
    }

}
