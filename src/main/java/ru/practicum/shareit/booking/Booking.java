package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @EqualsAndHashCode.Include
    @Column(name = "booker_id", nullable = false)
    private Long bookerId;

    @EqualsAndHashCode.Include
    @Column(name = "start_ts", nullable = false)
    private LocalDateTime start;

    @EqualsAndHashCode.Include
    @Column(name = "end_ts", nullable = false)
    private LocalDateTime end;

    @EqualsAndHashCode.Include
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.WAITING;
}