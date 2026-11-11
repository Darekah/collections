package com.project.collections.service.manga;

import com.project.collections.dto.manga.MangaBatchResponse;
import com.project.collections.dto.manga.MangaRequest;
import com.project.collections.dto.manga.MangaResponse;
import com.project.collections.exception.ConflictException;
import com.project.collections.exception.ItemNotFoundException;
import com.project.collections.model.manga.Manga;
import com.project.collections.model.manga.MangaCollection;
import com.project.collections.repository.manga.MangaCollectionRepository;
import com.project.collections.repository.manga.MangaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic for Manga CRUD and upsert operations.
 */
@Service
@Transactional
public class MangaService {

    private final MangaRepository mangaRepository;
    private final MangaCollectionRepository mangaCollectionRepository;

    public MangaService(MangaRepository mangaRepository,
                        MangaCollectionRepository mangaCollectionRepository) {
        this.mangaRepository = mangaRepository;
        this.mangaCollectionRepository = mangaCollectionRepository;
    }

    @Transactional(readOnly = true)
    public List<MangaResponse> findAll() {
        return mangaRepository.findAll()
                .stream()
                .map(MangaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MangaResponse findById(Long id) {
        return mangaRepository.findById(id)
                .map(MangaResponse::from)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    public MangaBatchResponse upsert(List<MangaRequest> requests) {
        int createdCount = 0;
        int updatedCount = 0;
        List<MangaResponse> processed = new ArrayList<>(requests.size());

        for (MangaRequest request : requests) {
            Manga manga = findTarget(request);
            if (manga == null) {
                manga = createEntity(request);
                createdCount++;
            } else {
                applyRequest(manga, request);
                updatedCount++;
            }
            Manga saved = mangaRepository.save(manga);
            processed.add(MangaResponse.from(saved));
        }

        return new MangaBatchResponse(processed.size(), createdCount, updatedCount, processed);
    }

    public void delete(Long id) {
        if (!mangaRepository.existsById(id)) {
            throw new ItemNotFoundException(id);
        }
        mangaRepository.deleteById(id);
    }

    /**
     * Resolves the manga a request targets: by id first, then by ISBN when id is absent.
     * Returns null when the request describes a new manga.
     */
    private Manga findTarget(MangaRequest request) {
        String isbn = Manga.normalizeIsbn(request.isbn());
        if (request.id() != null) {
            Manga byId = mangaRepository.findById(request.id())
                    .orElseThrow(() -> new ItemNotFoundException(request.id()));
            if (isbn != null) {
                mangaRepository.findByIsbn(isbn)
                        .filter(other -> !other.getId().equals(byId.getId()))
                        .ifPresent(other -> {
                            throw new ConflictException(
                                    "ISBN " + isbn + " already belongs to manga with id: " + other.getId()
                            );
                        });
            }
            return byId;
        }
        if (isbn != null) {
            return mangaRepository.findByIsbn(isbn).orElse(null);
        }
        return null;
    }

    private Manga createEntity(MangaRequest request) {
        Manga manga = new Manga(
                request.title(),
                request.originalTitle(),
                request.description(),
                request.year(),
                request.coverUrl(),
                request.author(),
                request.artist(),
                request.volumeNumber(),
                request.isbn()
        );
        manga.setCollection(resolveCollection(request.collectionId()));
        return manga;
    }

    private void applyRequest(Manga manga, MangaRequest request) {
        manga.setTitle(request.title());
        manga.setOriginalTitle(request.originalTitle());
        manga.setDescription(request.description());
        manga.setYear(request.year());
        manga.setCoverUrl(request.coverUrl());
        manga.setAuthor(request.author());
        manga.setArtist(request.artist());
        manga.setVolumeNumber(request.volumeNumber());
        manga.setIsbn(request.isbn());
        manga.setCollection(resolveCollection(request.collectionId()));
    }

    private MangaCollection resolveCollection(Long collectionId) {
        if (collectionId == null) {
            return null;
        }
        return mangaCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ItemNotFoundException(collectionId));
    }

}
