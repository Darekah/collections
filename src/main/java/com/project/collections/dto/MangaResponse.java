package com.project.collections.dto;

import com.project.collections.model.ItemType;
import com.project.collections.model.Manga;

import java.time.Instant;

/**
 * Representation of a Manga returned by the API.
 */
public record MangaResponse(

        Long id,
        String title,
        String originalTitle,
        String description,
        ItemType type,
        Integer year,
        String coverUrl,
        String author,
        String artist,
        String publisher,
        Integer volumeNumber,
        Integer totalVolumes,
        String isbn,
        Integer pageCount,
        String format,
        Instant createdAt,
        Instant updatedAt

) {

    public static MangaResponse from(Manga manga) {
        return new MangaResponse(
                manga.getId(),
                manga.getTitle(),
                manga.getOriginalTitle(),
                manga.getDescription(),
                manga.getType(),
                manga.getYear(),
                manga.getCoverUrl(),
                manga.getAuthor(),
                manga.getArtist(),
                manga.getPublisher(),
                manga.getVolumeNumber(),
                manga.getTotalVolumes(),
                manga.getIsbn(),
                manga.getPageCount(),
                manga.getFormat(),
                manga.getCreatedAt(),
                manga.getUpdatedAt()
        );
    }

}
