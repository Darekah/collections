package com.project.collections.repository;

import com.project.collections.model.Manga;
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

    Optional<Manga> findByIsbn(String isbn);

}
