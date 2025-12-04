package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ToString.Include
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean available;

    @Column(name = "owner_id")
    private Long ownerId;
}