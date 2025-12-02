package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "booker_id", nullable = false)
    private Long bookerId;

    @Column(name = "start_ts", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_ts", nullable = false)
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status = BookingStatus.WAITING;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) &&
                Objects.equals(itemId, booking.itemId) &&
                Objects.equals(bookerId, booking.bookerId) &&
                Objects.equals(start, booking.start) &&
                Objects.equals(end, booking.end) &&
                status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId, bookerId, start, end, status);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", bookerId=" + bookerId +
                ", start=" + start +
                ", end=" + end +
                ", status=" + status +
                '}';
    }
}