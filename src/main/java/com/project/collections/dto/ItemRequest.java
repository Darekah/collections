package com.project.collections.dto;

import com.project.collections.model.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload accepted when creating or updating an item.
 */
public record ItemRequest(

        @NotBlank
        @Size(max = 255)
        String title,

        @Size(max = 2000)
        String description,

        @NotNull
        ItemType type,

        Integer year

) {
}
