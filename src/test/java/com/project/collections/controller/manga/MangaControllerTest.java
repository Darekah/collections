package com.project.collections.controller.manga;

import com.project.collections.dto.manga.MangaRequest;
import com.project.collections.model.manga.Manga;
import com.project.collections.model.manga.MangaCollection;
import com.project.collections.repository.manga.MangaCollectionRepository;
import com.project.collections.repository.manga.MangaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MangaControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MangaRepository mangaRepository;

    @Autowired
    private MangaCollectionRepository mangaCollectionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mangaRepository.deleteAll();
        mangaCollectionRepository.deleteAll();
    }

    @Test
    void saveAll_singleMangaObjectPayload_createsNewMangaAndReturns200() throws Exception {
        MangaRequest request = new MangaRequest(
                null,
                "Berserk",
                "ベルセルク",
                "Dark fantasy story of Guts",
                1989,
                "https://example.com/berserk.jpg",
                "Kentaro Miura",
                null,
                null,
                "9784592135746",
                null
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcessed", is(1)))
                .andExpect(jsonPath("$.createdCount", is(1)))
                .andExpect(jsonPath("$.updatedCount", is(0)))
                .andExpect(jsonPath("$.items[0].id", notNullValue()))
                .andExpect(jsonPath("$.items[0].title", is("Berserk")))
                .andExpect(jsonPath("$.items[0].originalTitle", is("ベルセルク")))
                .andExpect(jsonPath("$.items[0].author", is("Kentaro Miura")))
                .andExpect(jsonPath("$.items[0].type", is("MANGA")))
                .andExpect(jsonPath("$.items[0].isbn", is("9784592135746")))
                .andExpect(jsonPath("$.items[0].collectionId", nullValue()));
    }

    @Test
    void saveAll_multipleMangasArrayPayload_createsAllAndReturns200() throws Exception {
        List<MangaRequest> requests = List.of(
                new MangaRequest(null, "Naruto Vol 1", null, null, 1999, null, "Masashi Kishimoto", null, 1, "9781569319000", null),
                new MangaRequest(null, "Naruto Vol 2", null, null, 2000, null, "Masashi Kishimoto", null, 2, "9781591161783", null)
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcessed", is(2)))
                .andExpect(jsonPath("$.createdCount", is(2)))
                .andExpect(jsonPath("$.updatedCount", is(0)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].title", is("Naruto Vol 1")))
                .andExpect(jsonPath("$.items[1].title", is("Naruto Vol 2")));
    }

    @Test
    void saveAll_existingMangaWithId_updatesAndReturns200() throws Exception {
        Manga initial = new Manga(
                "One Piece", null, "Pirate adventure", 1997, null,
                "Eiichiro Oda", null, 1, "9781569319017"
        );
        Manga saved = mangaRepository.save(initial);

        MangaRequest updateRequest = new MangaRequest(
                saved.getId(),
                "One Piece: Romance Dawn",
                "ワンピース",
                "Updated synopsis",
                1997,
                "https://example.com/op1.jpg",
                "Eiichiro Oda",
                null,
                1,
                "9781569319017",
                null
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcessed", is(1)))
                .andExpect(jsonPath("$.createdCount", is(0)))
                .andExpect(jsonPath("$.updatedCount", is(1)))
                .andExpect(jsonPath("$.items[0].id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.items[0].title", is("One Piece: Romance Dawn")))
                .andExpect(jsonPath("$.items[0].originalTitle", is("ワンピース")));
    }

    @Test
    void saveAll_volumeWithCollectionId_linksToCollection() throws Exception {
        MangaCollection collection = mangaCollectionRepository.save(new MangaCollection(
                "Berserk (Complete)", null, null, null, null,
                "Kentaro Miura", null
        ));

        MangaRequest request = new MangaRequest(
                null, "Berserk Vol. 3", null, null, 1989, null,
                "Kentaro Miura", null, 3, "9784592135791", collection.getId()
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdCount", is(1)))
                .andExpect(jsonPath("$.items[0].volumeNumber", is(3)))
                .andExpect(jsonPath("$.items[0].collectionId", is(collection.getId().intValue())));
    }

    @Test
    void saveAll_unknownCollectionId_returns404() throws Exception {
        MangaRequest request = new MangaRequest(
                null, "Orphan Volume", null, null, null, null,
                "Author", null, 1, null, 99999L
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveAll_nonExistentId_returns404() throws Exception {
        MangaRequest request = new MangaRequest(
                99999L, "Ghost Manga", null, null, null, null, "Unknown", null, null, null, null
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Item not found with id: 99999")));
    }

    @Test
    void saveAll_blankTitleOrAuthor_returns400() throws Exception {
        MangaRequest invalid = new MangaRequest(
                null, "", null, null, null, null, "", null, null, null, null
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(400)));
    }

    @Test
    void findAll_returnsListOfSavedMangas() throws Exception {
        mangaRepository.save(new Manga("Slam Dunk", null, null, 1990, null, "Takehiko Inoue", null, 1, null));

        mockMvc.perform(get("/api/mangas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Slam Dunk")))
                .andExpect(jsonPath("$[0].author", is("Takehiko Inoue")));
    }

    @Test
    void findById_existingId_returnsManga() throws Exception {
        Manga saved = mangaRepository.save(new Manga("Akira", null, null, 1982, null, "Katsuhiro Otomo", null, 1, null));

        mockMvc.perform(get("/api/mangas/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Akira")))
                .andExpect(jsonPath("$.author", is("Katsuhiro Otomo")));
    }

    @Test
    void saveAll_isbnOnlyPayload_updatesExistingManga() throws Exception {
        Manga saved = mangaRepository.save(new Manga(
                "Vinland Saga", null, null, 2005, null,
                "Makoto Yukimura", null, 1, "978-4-06-388027-4"
        ));
        assertEquals("9784063880274", saved.getIsbn());

        MangaRequest request = new MangaRequest(
                null, "Vinland Saga Vol. 1", null, "Updated", 2005, null,
                "Makoto Yukimura", null, 1, "9784063880274", null
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdCount", is(0)))
                .andExpect(jsonPath("$.updatedCount", is(1)))
                .andExpect(jsonPath("$.items[0].id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.items[0].title", is("Vinland Saga Vol. 1")));
    }

    @Test
    void saveAll_idWithForeignIsbn_returns409() throws Exception {
        mangaRepository.save(new Manga(
                "Manga A", null, null, null, null, "Author A", null, null, "1111111111111"
        ));
        Manga other = mangaRepository.save(new Manga(
                "Manga B", null, null, null, null, "Author B", null, null, "2222222222222"
        ));

        MangaRequest request = new MangaRequest(
                other.getId(), "Manga B", null, null, null, null, "Author B", null, null, "1111111111111", null
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is(409)));
    }

    @Test
    void saveAll_unknownIsbn_createsNewManga() throws Exception {
        MangaRequest request = new MangaRequest(
                null, "New Manga", null, null, 2020, null, "New Author", null, 1, "9999999999999", null
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdCount", is(1)))
                .andExpect(jsonPath("$.items[0].isbn", is("9999999999999")));
    }

    @Test
    void delete_existingId_removesManga() throws Exception {
        Manga saved = mangaRepository.save(new Manga("Monster", null, null, 1994, null, "Naoki Urasawa", null, 1, null));

        mockMvc.perform(delete("/api/mangas/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertFalse(mangaRepository.existsById(saved.getId()));
    }

}
