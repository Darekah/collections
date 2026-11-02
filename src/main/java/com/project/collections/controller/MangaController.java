package com.project.collections.controller;

import com.project.collections.dto.MangaBatchResponse;
import com.project.collections.dto.MangaRequest;
import com.project.collections.dto.MangaResponse;
import com.project.collections.service.MangaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for manga resources under /api/mangas.
 * Supports individual item lookups, deletions, and flexible single/batch upserts.
 */
@RestController
@RequestMapping("/api/mangas")
@Validated
public class MangaController {

    private final MangaService mangaService;

    public MangaController(MangaService mangaService) {
        this.mangaService = mangaService;
    }

    @GetMapping
    public ResponseEntity<List<MangaResponse>> findAll() {
        return ResponseEntity.ok(mangaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MangaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mangaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MangaBatchResponse> saveAll(@RequestBody List<@Valid MangaRequest> requests) {
        return ResponseEntity.ok(mangaService.upsert(requests));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mangaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
