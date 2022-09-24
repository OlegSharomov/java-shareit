package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.dto.BookingState.ALL;
import static ru.practicum.shareit.booking.dto.BookingState.CURRENT;
import static ru.practicum.shareit.booking.dto.BookingState.FUTURE;
import static ru.practicum.shareit.booking.dto.BookingState.PAST;
import static ru.practicum.shareit.booking.dto.BookingState.REJECTED;
import static ru.practicum.shareit.booking.dto.BookingState.WAITING;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    BookingClient bookingClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    ResponseEntity<Object> resp = new ResponseEntity<>(HttpStatus.OK);
    LocalDateTime minuteOfToday = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(), LocalDateTime.MIN.getHour(), LocalDateTime.now().getMinute(),
            LocalDateTime.now().getSecond());
    BookItemRequestDto bookingDto1 = BookItemRequestDto.builder().start(minuteOfToday.plusDays(1))
            .end(minuteOfToday.plusDays(2)).build();

    // bookItem
    @Test
    public void shouldCallCreateBookingAndReturnBookingDtoAnswer() throws Exception {
        when(bookingClient.bookItem(eq(1L), any(BookItemRequestDto.class))).thenReturn(resp);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1))
                .bookItem(eq(1L), any(BookItemRequestDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenStartIsPast() throws Exception {
        BookItemRequestDto bookingDtoPast = BookItemRequestDto.builder().start(minuteOfToday.minusDays(1))
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
                        .contains("'start' cannot be in the past")));
        Mockito.verify(bookingClient, Mockito.times(0))
                .bookItem(any(Long.class), any(BookItemRequestDto.class));
    }

    @Test
    public void shouldThrowExceptionWhenEndIsPast() throws Exception {
        BookItemRequestDto bookingDtoPast = BookItemRequestDto.builder().start(minuteOfToday.plusDays(1))
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
                        .contains("'end' cannot be in the past or present")));
        Mockito.verify(bookingClient, Mockito.times(0))
                .bookItem(any(Long.class), any(BookItemRequestDto.class));
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
        Mockito.verify(bookingClient, Mockito.times(0))
                .bookItem(any(Long.class), any(BookItemRequestDto.class));
    }

    // changeRequestStatus
    @Test
    public void shouldCallBookingServiceUpdateAnrReturnBookingDtoAnswerFull() throws Exception {
        when(bookingClient.updateBookingStatus(1L, 1L, false)).thenReturn(resp);
        mockMvc.perform(patch("/bookings/{bookingId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1))
                .updateBookingStatus(1L, 1L, false);
    }

    @Test
    public void shouldThrowExceptionWhenRequestParamNotExists() throws Exception {
        when(bookingClient.updateBookingStatus(1L, 1L, false)).thenReturn(resp);
        mockMvc.perform(patch("/bookings/{bookingId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MissingServletRequestParameterException));
        Mockito.verify(bookingClient, Mockito.times(0))
                .updateBookingStatus(any(Long.class), any(Long.class), any(Boolean.class));
    }

    // getBooking
    @Test
    public void shouldReturnBookingDtoAnswerFull() throws Exception {
        Mockito.when(bookingClient.getBooking(1L, 1L)).thenReturn(resp);
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getBooking(1L, 1L);
    }

    @Test
    public void shouldThrowExceptionWhenBookingIdNegative() throws Exception {
        Mockito.when(bookingClient.getBooking(1L, 1L)).thenReturn(resp);
        mockMvc.perform(get("/bookings/{bookingId}", "-1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("'bookingId' must be positive")));
        Mockito.verify(bookingClient, Mockito.times(0)).getBooking(any(Long.class), any(Long.class));

    }

    // getBookings
    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenAllRequestParametersPresent() throws Exception {
        when(bookingClient.getBookings(1L, ALL, 0, 5)).thenReturn(resp);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getBookings(1L, ALL, 0, 5);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParametersNotExists() throws Exception {
        when(bookingClient.getBookings(1L, ALL, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getBookings(1L, ALL, 0, 10);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParameterIsCurrent() throws Exception {
        when(bookingClient.getBookings(1L, CURRENT, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "CURRENT")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getBookings(1L, CURRENT, 0, 10);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParameterIsPast() throws Exception {
        when(bookingClient.getBookings(1L, PAST, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "PAST")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getBookings(1L, PAST, 0, 10);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParameterIsFuture() throws Exception {
        when(bookingClient.getBookings(1L, FUTURE, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getBookings(1L, FUTURE, 0, 10);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParameterIsWaiting() throws Exception {
        when(bookingClient.getBookings(1L, WAITING, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getBookings(1L, WAITING, 0, 10);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullWhenWhenRequestParameterIsRejected() throws Exception {
        when(bookingClient.getBookings(1L, REJECTED, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "REJECTED")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getBookings(1L, REJECTED, 0, 10);
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
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("Unknown state: UNSUPPORTED_STATUS")));
        Mockito.verify(bookingClient, Mockito.times(0)).getBookings(any(Long.class),
                any(BookingState.class), any(Integer.class), any(Integer.class));
    }

    // getAllBookingsOfItemsOwner
    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenAllRequestParametersPresent() throws Exception {
        when(bookingClient.getAllBookingsOfItemsOwner(1L, ALL, 0, 5)).thenReturn(resp);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getAllBookingsOfItemsOwner(1L, ALL, 0, 5);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParametersOnlyState() throws Exception {
        when(bookingClient.getAllBookingsOfItemsOwner(1L, ALL, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getAllBookingsOfItemsOwner(1L, ALL, 0, 10);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParameterIsCurrent() throws Exception {
        when(bookingClient.getAllBookingsOfItemsOwner(1L, CURRENT, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "CURRENT")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getAllBookingsOfItemsOwner(1L, CURRENT, 0, 10);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParameterIsPast() throws Exception {
        when(bookingClient.getAllBookingsOfItemsOwner(1L, PAST, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "PAST")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getAllBookingsOfItemsOwner(1L, PAST, 0, 10);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParameterIsFuture() throws Exception {
        when(bookingClient.getAllBookingsOfItemsOwner(1L, FUTURE, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getAllBookingsOfItemsOwner(1L, FUTURE, 0, 10);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParameterIsWaiting() throws Exception {
        when(bookingClient.getAllBookingsOfItemsOwner(1L, WAITING, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getAllBookingsOfItemsOwner(1L, WAITING, 0, 10);
    }

    @Test
    public void shouldReturnListBookingDtoAnswerFullOfOwnerWhenWhenRequestParameterIsRejected() throws Exception {
        when(bookingClient.getAllBookingsOfItemsOwner(1L, REJECTED, null, null)).thenReturn(resp);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "REJECTED")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(bookingClient, Mockito.times(1)).getAllBookingsOfItemsOwner(1L, REJECTED, 0, 10);
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
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("Unknown state: UNSUPPORTED_STATUS")));
        Mockito.verify(bookingClient, Mockito.times(0)).getAllBookingsOfItemsOwner(1L, ALL, 0, 10);
    }
}
