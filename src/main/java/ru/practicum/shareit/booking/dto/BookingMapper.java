package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "itemId", target = "item.id")
    Booking toBooking(BookingDto bookingDto);

    @Mapping(source = "item.id", target = "item")
    BookingDtoAnswer toBookingDtoAnswer(Booking booking);

    BookingDtoAnswerFull toBookingDtoAnswerFull(Booking booking);

    @Mapping(source = "booker.id", target = "bookerId")
    BookingDtoWithBookerId toBookingDtoWithBookerId(Booking booking);
}
