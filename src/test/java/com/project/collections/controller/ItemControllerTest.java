package com.project.collections.controller;

import com.project.collections.model.ItemType;
import com.project.collections.model.manga.Manga;
import com.project.collections.repository.ItemRepository;
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

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ItemControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ItemRepository itemRepository;

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
    void findAll_returnsMangasThroughGenericEndpoint() throws Exception {
        mangaRepository.save(new Manga("Akira", null, null, 1982, null, "Katsuhiro Otomo", null, 1, null));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is("MANGA")))
                .andExpect(jsonPath("$[0].title", is("Akira")))
                .andExpect(jsonPath("$[0].originalTitle", nullValue()))
                .andExpect(jsonPath("$[0].coverUrl", nullValue()));
    }

    @Test
    void findById_mangaId_returnsSameRowAsMangaEndpoint() throws Exception {
        Manga saved = mangaRepository.save(new Manga("Monster", null, null, 1994, null, "Naoki Urasawa", null, 1, null));

        mockMvc.perform(get("/api/items/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.type", is(ItemType.MANGA.name())));
    }

    @Test
    void create_postNotAllowed_returns405() throws Exception {
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "x", "type", "BOOK"))))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void delete_existingMangaId_removesItemAndChildRow() throws Exception {
        Manga saved = mangaRepository.save(new Manga("Slam Dunk", null, null, 1990, null, "Takehiko Inoue", null, 1, null));

        mockMvc.perform(delete("/api/items/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertFalse(itemRepository.existsById(saved.getId()));
        assertFalse(mangaRepository.existsById(saved.getId()));
    }

}
