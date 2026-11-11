package com.project.collections.dto.manga;

import java.util.List;

/**
 * Summary and detail response for batch/upsert operations on Manga items.
 */
public record MangaBatchResponse(

        int totalProcessed,
        int createdCount,
        int updatedCount,
        List<MangaResponse> items

) {
}
