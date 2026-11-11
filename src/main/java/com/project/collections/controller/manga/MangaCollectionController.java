package com.project.collections.controller.manga;

import com.project.collections.dto.manga.MangaCollectionRequest;
import com.project.collections.dto.manga.MangaCollectionResponse;
import com.project.collections.service.manga.MangaCollectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * REST endpoints for manga collection resources under /api/manga-collections.
 */
@RestController
@RequestMapping("/api/manga-collections")
public class MangaCollectionController {

    private final MangaCollectionService mangaCollectionService;

    public MangaCollectionController(MangaCollectionService mangaCollectionService) {
        this.mangaCollectionService = mangaCollectionService;
    }

    @GetMapping
    public ResponseEntity<List<MangaCollectionResponse>> findAll() {
        return ResponseEntity.ok(mangaCollectionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MangaCollectionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mangaCollectionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MangaCollectionResponse> create(@Valid @RequestBody MangaCollectionRequest request) {
        MangaCollectionResponse created = mangaCollectionService.create(request);
        return ResponseEntity.created(URI.create("/api/manga-collections/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MangaCollectionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MangaCollectionRequest request
    ) {
        return ResponseEntity.ok(mangaCollectionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mangaCollectionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
