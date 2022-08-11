package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;


@Builder
@Getter
@Setter
@ToString
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private final Long id;
    @Column(name = "start_date")
    private LocalDate start;
    @Column(name = "end_date")
    private LocalDate end;
    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;
    @OneToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;    // пользователь, который осуществляет бронирование
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;   /* - статус бронирования (ожидает одобрения, подтверждено владельцем,
                                     отклонено владельцем или отменено создателем) */
}