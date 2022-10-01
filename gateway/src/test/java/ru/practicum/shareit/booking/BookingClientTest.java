package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static ru.practicum.shareit.booking.dto.BookingState.ALL;

@RunWith(SpringRunner.class)
@RestClientTest(BookingClient.class)
public class BookingClientTest {
    @Autowired
    BookingClient bookingClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper mapper;

    BookItemRequestDto booking1 = BookItemRequestDto.builder().start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2)).build();

    @AfterEach
    void checkCallParameters() {
        this.mockServer.verify();
    }

    private void sendRequest(String addUrl, HttpMethod httpMethod) {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/bookings" + addUrl))
                .andExpect(method(httpMethod))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(1L)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    public void shouldCallGetBookings() {
        String url = "?state=ALL&from=0&size=10";
        sendRequest(url, GET);
        ResponseEntity<Object> result = bookingClient.getBookings(1L, ALL, 0, 10);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }


    @Test
    public void shouldCallBookItem() throws JsonProcessingException {
        mockServer.expect(ExpectedCount.once(), requestTo("http://localhost:9090/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(booking1)))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(1L)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        ResponseEntity<Object> result = bookingClient.bookItem(1L, booking1);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallChangeRequestStatus() {
        sendRequest("/1?approved=false", PATCH);
        ResponseEntity<Object> result = bookingClient.updateBookingStatus(1L, 1L, false);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallGetBooking() {
        sendRequest("/1", GET);
        ResponseEntity<Object> result = bookingClient.getBooking(1L, 1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void shouldCallGetAllBookingsOfItemsOwner() {
        sendRequest("/owner?state=ALL&from=0&size=10", GET);
        ResponseEntity<Object> result = bookingClient.getAllBookingsOfItemsOwner(1L, ALL, 0, 10);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
