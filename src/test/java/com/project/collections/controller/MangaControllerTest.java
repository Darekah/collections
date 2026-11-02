package com.project.collections.controller;

import com.project.collections.dto.MangaRequest;
import com.project.collections.model.Manga;
import com.project.collections.repository.MangaRepository;
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
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mangaRepository.deleteAll();
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
                "Hakusensha",
                1,
                null,
                "9784592135746",
                224,
                "TANKOBON"
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
                .andExpect(jsonPath("$.items[0].isbn", is("9784592135746")));
    }

    @Test
    void saveAll_multipleMangasArrayPayload_createsAllAndReturns200() throws Exception {
        List<MangaRequest> requests = List.of(
                new MangaRequest(null, "Naruto Vol 1", null, null, 1999, null, "Masashi Kishimoto", null, "Shueisha", 1, 72, "9781569319000", 192, "TANKOBON"),
                new MangaRequest(null, "Naruto Vol 2", null, null, 2000, null, "Masashi Kishimoto", null, "Shueisha", 2, 72, "9781591161783", 208, "TANKOBON")
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
                "Eiichiro Oda", null, "Shueisha", 1, null, "9781569319017", 200, "TANKOBON"
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
                "Planeta Cómic",
                1,
                110,
                "9781569319017",
                216,
                "KANZENBAN"
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
                .andExpect(jsonPath("$.items[0].originalTitle", is("ワンピース")))
                .andExpect(jsonPath("$.items[0].publisher", is("Planeta Cómic")))
                .andExpect(jsonPath("$.items[0].pageCount", is(216)))
                .andExpect(jsonPath("$.items[0].format", is("KANZENBAN")));
    }

    @Test
    void saveAll_nonExistentId_returns404() throws Exception {
        MangaRequest request = new MangaRequest(
                99999L, "Ghost Manga", null, null, null, null, "Unknown", null, null, null, null, null, null, null
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
                null, "", null, null, null, null, "", null, null, null, null, null, null, null
        );

        mockMvc.perform(post("/api/mangas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(400)));
    }

    @Test
    void findAll_returnsListOfSavedMangas() throws Exception {
        mangaRepository.save(new Manga("Slam Dunk", null, null, 1990, null, "Takehiko Inoue", null, null, 1, 31, null, null, null));

        mockMvc.perform(get("/api/mangas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Slam Dunk")))
                .andExpect(jsonPath("$[0].author", is("Takehiko Inoue")));
    }

    @Test
    void findById_existingId_returnsManga() throws Exception {
        Manga saved = mangaRepository.save(new Manga("Akira", null, null, 1982, null, "Katsuhiro Otomo", null, null, 1, 6, null, null, null));

        mockMvc.perform(get("/api/mangas/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Akira")))
                .andExpect(jsonPath("$.author", is("Katsuhiro Otomo")));
    }

    @Test
    void delete_existingId_removesManga() throws Exception {
        Manga saved = mangaRepository.save(new Manga("Monster", null, null, 1994, null, "Naoki Urasawa", null, null, 1, 18, null, null, null));

        mockMvc.perform(delete("/api/mangas/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertFalse(mangaRepository.existsById(saved.getId()));
    }

}
