package com.project.collections.repository.manga;

import com.project.collections.model.manga.Manga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for Manga entities.
 */
@Repository
public interface MangaRepository extends JpaRepository<Manga, Long> {

    List<Manga> findByAuthorContainingIgnoreCase(String author);

    List<Manga> findByCollection_Id(Long collectionId);

    Optional<Manga> findByIsbn(String isbn);

}
