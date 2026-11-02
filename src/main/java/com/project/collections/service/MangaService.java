package com.project.collections.service;

import com.project.collections.dto.MangaBatchResponse;
import com.project.collections.dto.MangaRequest;
import com.project.collections.dto.MangaResponse;
import com.project.collections.exception.ItemNotFoundException;
import com.project.collections.model.Manga;
import com.project.collections.repository.MangaRepository;
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

    public MangaService(MangaRepository mangaRepository) {
        this.mangaRepository = mangaRepository;
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
            Manga manga;
            if (request.id() != null) {
                manga = mangaRepository.findById(request.id())
                        .orElseThrow(() -> new ItemNotFoundException(request.id()));
                applyRequest(manga, request);
                updatedCount++;
            } else {
                manga = createEntity(request);
                createdCount++;
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

    private Manga createEntity(MangaRequest request) {
        return new Manga(
                request.title(),
                request.originalTitle(),
                request.description(),
                request.year(),
                request.coverUrl(),
                request.author(),
                request.artist(),
                request.publisher(),
                request.volumeNumber(),
                request.totalVolumes(),
                request.isbn(),
                request.pageCount(),
                request.format()
        );
    }

    private void applyRequest(Manga manga, MangaRequest request) {
        manga.setTitle(request.title());
        manga.setOriginalTitle(request.originalTitle());
        manga.setDescription(request.description());
        manga.setYear(request.year());
        manga.setCoverUrl(request.coverUrl());
        manga.setAuthor(request.author());
        manga.setArtist(request.artist());
        manga.setPublisher(request.publisher());
        manga.setVolumeNumber(request.volumeNumber());
        manga.setTotalVolumes(request.totalVolumes());
        manga.setIsbn(request.isbn());
        manga.setPageCount(request.pageCount());
        manga.setFormat(request.format());
    }

}
