package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING,    // — new booking, WAITING approval,
    APPROVED,   // - booking APPROVED by the owner
    REJECTED,   // — booking REJECTED by the owner
    CANCELED    // — booking CANCELED by the creator
}