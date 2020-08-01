package com.example.demo.controller;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {

  private final ItemRepository repository;

  public ItemController(ItemRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public ResponseEntity<List<Item>> getItems() {
    return ResponseEntity.ok(repository.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Item> getItemById(@PathVariable Long id) {
    return ResponseEntity.of(repository.findById(id));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
    List<Item> items = repository.findByName(name);
    return items == null || items.isEmpty() ? ResponseEntity.notFound().build()
        : ResponseEntity.ok(items);

  }

}