package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository {

    private final List<Item> items = new ArrayList<>();

    public Item save(Item item) {
        item.setId((long) (items.size() + 1));
        items.add(item);
        return item;
    }

    public List<Item> findAll() {
        return new ArrayList<>(items);
    }

    public Item findById(Long id) {
        return items.stream().filter(item -> item.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Item> searchByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String searchText = text.trim().toLowerCase();

        System.out.println("Поиск текста: '" + searchText + "'");

        List<Item> result = items.stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> {
                    boolean matches = false;

                    if (item.getName() != null) {
                        String lowerName = item.getName().toLowerCase();
                        matches = lowerName.contains(searchText);
                        if (matches) System.out.println("Найдено в названии: " + item.getName());
                    }

                    if (!matches && item.getDescription() != null) {
                        String lowerDesc = item.getDescription().toLowerCase();
                        matches = lowerDesc.contains(searchText);
                        if (matches) System.out.println("Найдено в описании: " + item.getDescription());
                    }

                    return matches;
                })
                .collect(Collectors.toList());

        System.out.println("Найдено предметов: " + result.size());
        return result;
    }

    public List<Item> findAllByOwner(Long ownerId) {
        return items.stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        items.removeIf(item -> item.getId().equals(id));
    }
}
