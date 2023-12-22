package com.abhay.itemservice;

import com.abhay.itemservice.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// class to insert sample items
@Component
@Slf4j
@ConditionalOnProperty(prefix = "spring.profiles", name = "active", havingValue = "dev")
public class InsertSampleItemsOnStartup implements ApplicationRunner {
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String url = "http://localhost:9001/addItem";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, String>> itemsToAdd = List.of(
                createItem("Macbook pro 16 inch","Apple macbook","1500.00","12"),
                createItem("Realme buds wireless 3","wireless earphones","40.00","21"),
                createItem("levis’s blue T-shirt","man’s top wear","25.00","18"));

        HttpEntity<List<Map<String, String>>> requestEntity = new HttpEntity<>(itemsToAdd, headers);
        ResponseEntity<Item[]> responseEntity = restTemplate.postForEntity(url, requestEntity, Item[].class);

        Item[] itemsSuccessfullyAdded = responseEntity.getBody();
        log.info("*** Below are the items added ***");
        Arrays.stream(itemsSuccessfullyAdded).forEach(item->log.info(item.getName()));
    }

    private Map<String, String> createItem(String name, String description, String price, String categoryId) {
        Map<String, String> items = new HashMap<>();
        items.put("name", name);
        items.put("description", description);
        items.put("price", price);
        items.put("categoryId", categoryId);

        return items;
    }
}
