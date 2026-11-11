package com.project.collections.controller.manga;

import com.project.collections.dto.manga.MangaCollectionRequest;
import com.project.collections.model.manga.MangaCollection;
import com.project.collections.repository.manga.MangaCollectionRepository;
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MangaCollectionControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MangaCollectionRepository mangaCollectionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mangaCollectionRepository.deleteAll();
    }

    @Test
    void create_validRequest_returns201WithLocation() throws Exception {
        MangaCollectionRequest request = new MangaCollectionRequest(
                "Berserk (Complete Edition)", "ベルセルク", "Dark fantasy series", 1989,
                "https://example.com/berserk.jpg", "Kentaro Miura", null
        );

        mockMvc.perform(post("/api/manga-collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Berserk (Complete Edition)")))
                .andExpect(jsonPath("$.type", is("MANGA_COLLECTION")))
                .andExpect(jsonPath("$.author", is("Kentaro Miura")));
    }

    @Test
    void create_blankTitleOrAuthor_returns400() throws Exception {
        MangaCollectionRequest request = new MangaCollectionRequest(
                "", null, null, null, null, "", null
        );

        mockMvc.perform(post("/api/manga-collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(400)));
    }

    @Test
    void findById_existingId_returnsCollection() throws Exception {
        MangaCollection saved = mangaCollectionRepository.save(new MangaCollection(
                "Naruto (Complete)", null, null, 1999, null, "Masashi Kishimoto", null
        ));

        mockMvc.perform(get("/api/manga-collections/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Naruto (Complete)")));
    }

    @Test
    void update_existingId_replacesFields() throws Exception {
        MangaCollection saved = mangaCollectionRepository.save(new MangaCollection(
                "One Piece (Complete)", null, null, null, null, "Eiichiro Oda", null
        ));

        MangaCollectionRequest request = new MangaCollectionRequest(
                "One Piece (Full set)", "ワンピース", "All volumes", 1997,
                null, "Eiichiro Oda", "Hiroyuki Takei"
        );

        mockMvc.perform(put("/api/manga-collections/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("One Piece (Full set)")))
                .andExpect(jsonPath("$.artist", is("Hiroyuki Takei")));
    }

    @Test
    void delete_existingId_removesCollection() throws Exception {
        MangaCollection saved = mangaCollectionRepository.save(new MangaCollection(
                "Akira (Complete)", null, null, 1982, null, "Katsuhiro Otomo", null
        ));

        mockMvc.perform(delete("/api/manga-collections/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertFalse(mangaCollectionRepository.existsById(saved.getId()));
    }

    @Test
    void findById_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/manga-collections/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)));
    }

}
