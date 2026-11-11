package com.project.collections.dto.manga;

import com.project.collections.model.ItemType;
import com.project.collections.model.manga.Manga;

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
        Integer volumeNumber,
        String isbn,
        Long collectionId,
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
                manga.getVolumeNumber(),
                manga.getIsbn(),
                manga.getCollectionId(),
                manga.getCreatedAt(),
                manga.getUpdatedAt()
        );
    }

}
