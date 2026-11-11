package com.project.collections.dto.manga;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload accepted when creating or updating a MangaCollection.
 */
public record MangaCollectionRequest(

        @NotBlank
        @Size(max = 255)
        String title,

        @Size(max = 255)
        String originalTitle,

        @Size(max = 2000)
        String description,

        Integer year,

        @Size(max = 500)
        String coverUrl,

        @NotBlank
        @Size(max = 255)
        String author,

        @Size(max = 255)
        String artist

) {
}
