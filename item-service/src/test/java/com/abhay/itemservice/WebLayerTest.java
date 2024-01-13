package com.abhay.itemservice;

import com.abhay.itemservice.entity.Item;
import com.abhay.itemservice.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;


@WebMvcTest
@Slf4j
public class WebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllItems() throws Exception {
        when(itemService.getAllItems()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/items")).andDo(print()).andExpect(content().string("[]"));
    }

    @Test
    public void testGetAllItemsNegative() throws Exception {
        when(itemService.getAllItems()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/itemsss")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void testFindItemsByName() throws Exception {
        when(itemService.getItemsByName(any(String.class))).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/item").param("name","dummy value")).andDo(print()).andExpect(content().string("[]"));
    }

    @Test
    public void testAddItem() throws Exception {
        when(itemService.addItem(any(Item.class))).thenReturn(new Item());
        List<Item> itemList = new ArrayList<>();
        Item item = new Item();
        item.setName("dummy item");
        item.setDescription("dummy description");
        item.setPrice(50.00);
        item.setCategoryId(12);
        itemList.add(item);
        log.info(objectMapper.writeValueAsString(itemList));
        mockMvc.perform(post("/addItem").content(objectMapper.writeValueAsString(itemList))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());
    }
}
