package com.project.collections.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload accepted when creating or updating a Manga.
 * Includes both common Item attributes and Manga-specific attributes.
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

        @Size(max = 100)
        String publisher,

        Integer volumeNumber,

        Integer totalVolumes,

        @Size(max = 20)
        String isbn,

        Integer pageCount,

        @Size(max = 50)
        String format

) {
}
