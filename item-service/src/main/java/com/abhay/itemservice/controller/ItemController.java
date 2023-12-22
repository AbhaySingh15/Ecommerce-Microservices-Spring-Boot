package com.abhay.itemservice.controller;

import com.abhay.itemservice.entity.Item;
import com.abhay.itemservice.service.ItemService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/items")
    public List<Item> getAllItems(){
//        var itemList = itemService.getAllItems();
//        for(Item item: itemList){
//            Link link = linkTo(methodOn(ItemController.class).findItemByName(item.getName())).withSelfRel();
//            item.add(link);
//        }
        return itemService.getAllItems();
    }

    @GetMapping("/item")
    public List<Item> findItemsByName(@RequestParam String... name){
        return itemService.getItemsByName(name);
    }

    @PostMapping("/addItem")
    public ResponseEntity<List<Item>> addItem(@RequestBody List<@Valid Item> itemsToAdd){
        List<Item> itemsSuccessfullyAdded = new ArrayList<>();
        for(Item item: itemsToAdd){
            log.info(item.getCategoryId()+"inside controller");
            if(itemService.addItem(item)!=null)
                itemsSuccessfullyAdded.add(item);
        }
        return ResponseEntity.ok(itemsSuccessfullyAdded);
    }

    @GetMapping("/item/{name}")
    public Item findItemByName(@PathVariable("name") String name){
        return itemService.getItemByName(name);
    }
}
