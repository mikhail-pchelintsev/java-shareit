package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT * FROM bookings b WHERE b.booker_id = :bookerId ORDER BY b.start_ts DESC",
            nativeQuery = true)
    List<Booking> findByBookerIdOrderByStartDesc(@Param("bookerId") Long bookerId);

    @Query(value = "SELECT * FROM bookings b WHERE b.booker_id = :bookerId AND b.start_ts <= :now AND b.end_ts" +
            " >= :now ORDER BY b.start_ts DESC", nativeQuery = true)
    List<Booking> findByBookerIdAndCurrent(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query(value = "SELECT * FROM bookings b WHERE b.booker_id = :bookerId AND b.end_ts < :now ORDER BY b.start_ts DESC"
            , nativeQuery = true)
    List<Booking> findByBookerIdAndPast(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query(value = "SELECT * FROM bookings b WHERE b.booker_id = :bookerId AND b.start_ts > :now ORDER BY b.start_ts" +
            " DESC", nativeQuery = true)
    List<Booking> findByBookerIdAndFuture(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query(value = "SELECT * FROM bookings b WHERE b.booker_id = :bookerId AND b.status = :status ORDER BY b.start_ts" +
            " DESC", nativeQuery = true)
    List<Booking> findByBookerIdAndStatus(@Param("bookerId") Long bookerId, @Param("status") String status);

    // owner queries: join with items table
    @Query(value = "SELECT b.* FROM bookings b JOIN items i ON b.item_id = i.id WHERE i.owner_id = :ownerId ORDER" +
            " BY b.start_ts DESC", nativeQuery = true)
    List<Booking> findByOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT b.* FROM bookings b JOIN items i ON b.item_id = i.id WHERE i.owner_id = :ownerId AND " +
            " b.start_ts <= :now AND b.end_ts >= :now ORDER BY b.start_ts DESC", nativeQuery = true)
    List<Booking> findByOwnerIdAndCurrent(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query(value = "SELECT b.* FROM bookings b JOIN items i ON b.item_id = i.id WHERE i.owner_id = :ownerId AND" +
            " b.end_ts < :now ORDER BY b.start_ts DESC", nativeQuery = true)
    List<Booking> findByOwnerIdAndPast(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query(value = "SELECT b.* FROM bookings b JOIN items i ON b.item_id = i.id WHERE i.owner_id = :ownerId AND" +
            " b.start_ts > :now ORDER BY b.start_ts DESC", nativeQuery = true)
    List<Booking> findByOwnerIdAndFuture(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query(value = "SELECT b.* FROM bookings b JOIN items i ON b.item_id = i.id WHERE i.owner_id = :ownerId AND" +
            " b.status = :status ORDER BY b.start_ts DESC", nativeQuery = true)
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") String status);

    @Query(value = "SELECT * FROM bookings b WHERE b.item_id = :itemId AND b.end_ts < :now AND b.status = 'APPROVED' ORDER BY b.end_ts DESC",
            nativeQuery = true)
    List<Booking> findByItemIdAndEndBeforeOrderByEndDesc(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query(value = "SELECT * FROM bookings b WHERE b.item_id = :itemId AND b.start_ts > :now AND b.status = 'APPROVED' ORDER BY b.start_ts ASC",
            nativeQuery = true)
    List<Booking> findByItemIdAndStartAfterOrderByStartAsc(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);
}