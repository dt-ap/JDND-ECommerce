package com.example.demo.controller;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
  private static final List<Item> ITEMS = List.of(
      Item.of(1L, "item1", BigDecimal.valueOf(3.00), "item1 description"),
      Item.of(2L, "item2", BigDecimal.valueOf(4.00), "item2 description"),
      Item.of(3L, "item2", BigDecimal.valueOf(3.00), "item2 other description")
  );

  private ItemController controller;
  private final ItemRepository repository = mock(ItemRepository.class);

  @BeforeEach
  void setup() {
    controller = new ItemController(repository);

    when(repository.findAll()).thenReturn(ITEMS);
    when(repository.findById(anyLong())).thenAnswer(inv -> {
      var id = (Long) inv.getArgument(0);
      return ITEMS.stream().filter(i -> i.getId().equals(id)).findFirst();
    });
    when(repository.findByName(anyString())).thenAnswer(inv -> {
      var name = (String) inv.getArgument(0);
      return ITEMS.stream().filter(i -> i.getName().equals(name)).collect(Collectors.toList());
    });
  }

  @Test
  public void getItems() {
    var res = controller.getItems();
    var items = res.getBody();

    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(items);
    assertEquals(3, items.size());
  }

  @Test
  public void getItemByFalseName() {
    var name = "item3";
    var res = controller.getItemsByName(name);
    var items = res.getBody();

    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(items);
  }

  @Test
  public void getItemById() {
    var id = 2L;
    var res = controller.getItemById(id);
    var item = res.getBody();

    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(item);
    assertEquals("item2", item.getName());
    assertEquals(BigDecimal.valueOf(4.00), item.getPrice());
  }

  @Test
  public void getItemByFalseId() {
    var id = 4L;
    var res = controller.getItemById(id);

    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }
}
