package com.project.collections.repository.manga;

import com.project.collections.model.manga.MangaCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for MangaCollection entities.
 */
@Repository
public interface MangaCollectionRepository extends JpaRepository<MangaCollection, Long> {
}
