package com.project.collections.dto.manga;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload accepted when creating or updating a Manga (a volume/edition).
 * Includes both common Item attributes and Manga-specific attributes.
 * {@code collectionId} optionally links the volume to a MangaCollection
 * (see /api/manga-collections).
 */
public record MangaRequest(

        Long id,

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
        String artist,

        Integer volumeNumber,

        @Size(max = 20)
        String isbn,

        Long collectionId

) {
}
