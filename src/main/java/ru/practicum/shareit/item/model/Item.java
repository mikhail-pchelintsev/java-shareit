package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */@Getter
@Setter
@AllArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
}
