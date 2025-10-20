package ru.practicum.shareit.user;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private List<Item> items;
    private List<Booking> bookings;
    private boolean deleted;
}
