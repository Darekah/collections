package com.project.collections.service;

import com.project.collections.dto.ItemResponse;
import com.project.collections.exception.ItemNotFoundException;
import com.project.collections.model.Item;
import com.project.collections.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Read-only business logic for the generic item resource.
 * Items are abstract — creation and updates happen through type-specific services.
 * Exposes DTOs only, never entities.
 */
@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemResponse> findAll() {
        return itemRepository.findAll()
                .stream()
                .map(ItemResponse::from)
                .toList();
    }

    public ItemResponse findById(Long id) {
        return ItemResponse.from(findEntity(id));
    }

    @Transactional
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

}
