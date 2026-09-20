package com.project.collections.dto;

import com.project.collections.model.Item;
import com.project.collections.model.ItemType;

import java.time.Instant;

/**
 * Representation of an item returned by the API.
 */
public record ItemResponse(

        Long id,
        String title,
        String description,
        ItemType type,
        Integer year,
        Instant createdAt,
        Instant updatedAt

) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getType(),
                item.getYear(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }

}
