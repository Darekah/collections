package com.project.collections.model;

import com.project.collections.model.manga.Manga;
import com.project.collections.model.manga.MangaCollection;
import com.project.collections.repository.ItemRepository;
import com.project.collections.repository.manga.MangaCollectionRepository;
import com.project.collections.repository.manga.MangaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MangaRepository mangaRepository;

    @Autowired
    private MangaCollectionRepository mangaCollectionRepository;

    @Test
    void persistAndReadBack_manga_persistsJoinedTablesAndReadsPolymorphically() {
        Manga manga = new Manga(
                "Chainsaw Man, Vol. 1",
                "チェンソーマン 1",
                "Denji is a young man who works as a Devil Hunter...",
                2019,
                "https://example.com/csm1.jpg",
                "Tatsuki Fujimoto",
                null,
                1,
                "9788467941746"
        );

        Manga saved = entityManager.persistFlushFind(manga);

        assertNotNull(saved.getId());
        assertEquals("Chainsaw Man, Vol. 1", saved.getTitle());
        assertEquals("チェンソーマン 1", saved.getOriginalTitle());
        assertEquals(ItemType.MANGA, saved.getType());
        assertEquals("Tatsuki Fujimoto", saved.getAuthor());
        assertEquals(Integer.valueOf(1), saved.getVolumeNumber());
        assertEquals("9788467941746", saved.getIsbn());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());

        // Verify parent repository returns the polymorphic instance
        Optional<Item> itemLookup = itemRepository.findById(saved.getId());
        assertTrue(itemLookup.isPresent());
        assertInstanceOf(Manga.class, itemLookup.get());
        Manga casted = (Manga) itemLookup.get();
        assertEquals("Tatsuki Fujimoto", casted.getAuthor());

        // Verify child repository lookup by ISBN
        Optional<Manga> byIsbn = mangaRepository.findByIsbn("9788467941746");
        assertTrue(byIsbn.isPresent());
        assertEquals(saved.getId(), byIsbn.get().getId());
    }

    @Test
    void persistAndReadBack_mangaVolumeLinkedToCollection_persistsBothJoinedTables() {
        MangaCollection collection = entityManager.persistFlushFind(new MangaCollection(
                "Berserk (Complete)", null, null, null, null,
                "Kentaro Miura", null
        ));
        assertNotNull(collection.getId());
        assertEquals(ItemType.MANGA_COLLECTION, collection.getType());

        Manga volume = new Manga(
                "Berserk Vol. 3", null, null, 1989, null,
                "Kentaro Miura", null, 3, "9784592135791"
        );
        volume.setCollection(collection);
        Manga savedVolume = entityManager.persistFlushFind(volume);

        assertEquals(collection.getId(), savedVolume.getCollectionId());
        assertEquals(1, mangaRepository.findByCollection_Id(collection.getId()).size());
    }

}
