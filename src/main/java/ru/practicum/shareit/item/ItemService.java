package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;


@Service
public class ItemService {

    private final InMemoryItemRepository itemRepository;
    private final UserService userService;

    public ItemService(InMemoryItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    public Item addItem(Item item, Long userId) {
        if (item.getName() == null || item.getName().isEmpty() ||
                item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and description cannot be empty");
        }
        if (item.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Availability field must be provided");
        }

        User user = userService.getUser(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        item.setOwnerId(userId);
        return itemRepository.save(item);
    }

    public Item updateItem(Long itemId, Item item, Long userId) {
        Item existingItem = itemRepository.findById(itemId);
        if (existingItem == null || !existingItem.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found or unauthorized update attempt");
        }

        if (item.getName() != null && !item.getName().isEmpty()) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        return existingItem;
    }

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId);
    }

    public List<Item> getAllItemsByOwner(Long ownerId) {
        return itemRepository.findAllByOwner(ownerId);
    }

    public List<Item> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        return itemRepository.searchByText(text);
    }
}
