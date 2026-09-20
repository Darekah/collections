package com.project.collections.service;

import com.project.collections.dto.ItemRequest;
import com.project.collections.dto.ItemResponse;
import com.project.collections.exception.ItemNotFoundException;
import com.project.collections.model.Item;
import com.project.collections.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for item CRUD operations. Exposes DTOs only, never entities.
 */
@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> findAll() {
        return itemRepository.findAll()
                .stream()
                .map(ItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemResponse findById(Long id) {
        return ItemResponse.from(findEntity(id));
    }

    public ItemResponse create(ItemRequest request) {
        Item item = new Item(request.title(), request.description(), request.type(), request.year());
        return ItemResponse.from(itemRepository.save(item));
    }

    public ItemResponse update(Long id, ItemRequest request) {
        Item item = findEntity(id);
        applyRequest(item, request);
        return ItemResponse.from(itemRepository.save(item));
    }

    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException(id);
        }
        itemRepository.deleteById(id);
    }

    private Item findEntity(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    private void applyRequest(Item item, ItemRequest request) {
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setType(request.type());
        item.setYear(request.year());
    }

}
