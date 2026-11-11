package com.project.collections.dto.manga;

import com.project.collections.model.ItemType;
import com.project.collections.model.manga.MangaCollection;

import java.time.Instant;

/**
 * Representation of a MangaCollection returned by the API.
 */
public record MangaCollectionResponse(

        Long id,
        String title,
        String originalTitle,
        String description,
        ItemType type,
        Integer year,
        String coverUrl,
        String author,
        String artist,
        Instant createdAt,
        Instant updatedAt

) {

    public static MangaCollectionResponse from(MangaCollection collection) {
        return new MangaCollectionResponse(
                collection.getId(),
                collection.getTitle(),
                collection.getOriginalTitle(),
                collection.getDescription(),
                collection.getType(),
                collection.getYear(),
                collection.getCoverUrl(),
                collection.getAuthor(),
                collection.getArtist(),
                collection.getCreatedAt(),
                collection.getUpdatedAt()
        );
    }

}
