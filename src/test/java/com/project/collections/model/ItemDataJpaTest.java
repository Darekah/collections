package com.project.collections.model;

import com.project.collections.repository.ItemRepository;
import com.project.collections.repository.MangaRepository;
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
                "Norma Editorial",
                1,
                null,
                "9788467941746",
                192,
                "TANKOBON"
        );

        Manga saved = entityManager.persistFlushFind(manga);

        assertNotNull(saved.getId());
        assertEquals("Chainsaw Man, Vol. 1", saved.getTitle());
        assertEquals("チェンソーマン 1", saved.getOriginalTitle());
        assertEquals(ItemType.MANGA, saved.getType());
        assertEquals("Tatsuki Fujimoto", saved.getAuthor());
        assertEquals("Norma Editorial", saved.getPublisher());
        assertEquals(Integer.valueOf(1), saved.getVolumeNumber());
        assertEquals("9788467941746", saved.getIsbn());
        assertEquals(Integer.valueOf(192), saved.getPageCount());
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

}
