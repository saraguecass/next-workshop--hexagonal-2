package com.logistics.item;

import com.logistics.item.facade.dto.request.PatchItemRequestDTO;
import com.logistics.item.facade.dto.request.PostItemRequestDTO;
import com.logistics.item.repository.ItemRepository;
import com.logistics.item.repository.entity.ItemEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class ItemApplicationTests {

    static EasyRandom EASY_RANDOM;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;


    @BeforeAll
    public static void BeforeAll() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.stringLengthRange(10, 24);
        parameters.collectionSizeRange(5, 10);
        EASY_RANDOM = new EasyRandom(parameters);
    }

    @BeforeEach
    public void beforeEach() {
        log.info("Deleting items in database");
        itemRepository.deleteAll();
        List<ItemEntity> data = EASY_RANDOM.objects(ItemEntity.class, 20).toList();
        itemRepository.saveAll(data.stream().peek(e -> e.setId(UUID.randomUUID().toString())).toList());
    }

    ;

    @Test
    void getAllItems_shouldReturn200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/items"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getAllItems_shouldReturn204NoContent() throws Exception {
        itemRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders.get("/items"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void getItemById_shouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/123"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getItemById_shouldReturn200IfFound() throws Exception {
        String id = itemRepository.findAll().getFirst().getId();
        mockMvc.perform(MockMvcRequestBuilders.get("/items/".concat(id)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void createItem_shouldReturn201AndLocationHeader() throws Exception {
        PostItemRequestDTO dto = PostItemRequestDTO.builder()
                .name("Test item")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.matchesRegex(".*/items/.*")));
    }

    @Test
    void createItem_shouldReturn422IfMissingFields() throws Exception {
        PostItemRequestDTO dto = PostItemRequestDTO.builder()
                .build(); // falta name

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    void createItem_shouldReturn409IfDuplicated() throws Exception {
        String existingItemName = itemRepository.findAll().getFirst().getName();

        PostItemRequestDTO dto = PostItemRequestDTO.builder()
                .name(existingItemName)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void deleteItem_shouldReturn204IfSuccessful() throws Exception {
        String id = itemRepository.findAll().stream().findFirst().get().getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/".concat(id)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteItem_shouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/".concat(UUID.randomUUID().toString())))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteItem_shouldReturn422IfInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/invalid"))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    void patchItem_shouldReturn204IfSuccessful() throws Exception {
        String id = itemRepository.findAll().stream().findFirst().get().getId();
        PatchItemRequestDTO dto = PatchItemRequestDTO.builder()
                .name("New name")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.patch("/items/".concat(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void patchItem_shouldReturn404IfNotFound() throws Exception {
        PatchItemRequestDTO dto = PatchItemRequestDTO.builder()
                .build();

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void patchItem_shouldReturn422IfInvalidInput() throws Exception {
        String id = itemRepository.findAll().stream().findFirst().get().getId();
        PatchItemRequestDTO dto = PatchItemRequestDTO.builder()
                .name("")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.patch("/items/".concat(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}