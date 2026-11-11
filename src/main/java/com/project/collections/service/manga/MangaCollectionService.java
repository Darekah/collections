package com.project.collections.service.manga;

import com.project.collections.dto.manga.MangaCollectionRequest;
import com.project.collections.dto.manga.MangaCollectionResponse;
import com.project.collections.exception.ItemNotFoundException;
import com.project.collections.model.manga.MangaCollection;
import com.project.collections.repository.manga.MangaCollectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for MangaCollection CRUD operations.
 */
@Service
@Transactional
public class MangaCollectionService {

    private final MangaCollectionRepository mangaCollectionRepository;

    public MangaCollectionService(MangaCollectionRepository mangaCollectionRepository) {
        this.mangaCollectionRepository = mangaCollectionRepository;
    }

    @Transactional(readOnly = true)
    public List<MangaCollectionResponse> findAll() {
        return mangaCollectionRepository.findAll()
                .stream()
                .map(MangaCollectionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MangaCollectionResponse findById(Long id) {
        return MangaCollectionResponse.from(findEntity(id));
    }

    public MangaCollectionResponse create(MangaCollectionRequest request) {
        return MangaCollectionResponse.from(mangaCollectionRepository.save(newEntity(request)));
    }

    public MangaCollectionResponse update(Long id, MangaCollectionRequest request) {
        MangaCollection collection = findEntity(id);
        applyRequest(collection, request);
        return MangaCollectionResponse.from(mangaCollectionRepository.save(collection));
    }

    public void delete(Long id) {
        if (!mangaCollectionRepository.existsById(id)) {
            throw new ItemNotFoundException(id);
        }
        mangaCollectionRepository.deleteById(id);
    }

    private MangaCollection findEntity(Long id) {
        return mangaCollectionRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    private MangaCollection newEntity(MangaCollectionRequest request) {
        MangaCollection collection = new MangaCollection(
                request.title(),
                request.originalTitle(),
                request.description(),
                request.year(),
                request.coverUrl(),
                request.author(),
                request.artist()
        );
        return collection;
    }

    private void applyRequest(MangaCollection collection, MangaCollectionRequest request) {
        collection.setTitle(request.title());
        collection.setOriginalTitle(request.originalTitle());
        collection.setDescription(request.description());
        collection.setYear(request.year());
        collection.setCoverUrl(request.coverUrl());
        collection.setAuthor(request.author());
        collection.setArtist(request.artist());
    }

}
