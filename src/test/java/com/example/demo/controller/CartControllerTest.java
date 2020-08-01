package com.example.demo.controller;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
  private static final String USERNAME = "username";
  private static final List<Item> ITEMS = List.of(
      Item.of(1L, "item1", BigDecimal.valueOf(3.00), "item1 description"),
      Item.of(2L, "item2", BigDecimal.valueOf(4.00), "item2 description")
  );

  private CartController controller;
  private CartRepository repository = mock(CartRepository.class);
  private ItemRepository itemRepository = mock(ItemRepository.class);
  private UserRepository userRepository = mock(UserRepository.class);

  @BeforeEach
  void setup() {
    controller = new CartController(new CartService(repository, itemRepository, userRepository));
    var user = new User();
    user.setId(1L);
    user.setUsername(USERNAME);
    user.setPassword("password");

    var cart = new Cart();
    cart.setId(1L);
    cart.setUser(user);
    cart.setItems(new ArrayList<>(ITEMS));
    cart.setTotal(ITEMS.stream().map(Item::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add)); // Price total from items.
    user.setCart(cart);

    var order = UserOrder.createFromCart(cart);
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
    when(repository.findByUser(argThat(u -> u.getId() == user.getId()))).thenReturn(cart);
    when(itemRepository.findById(anyLong())).thenAnswer((inv) -> {
      var id = (Long) inv.getArgument(0);
      return ITEMS.stream().filter(i -> i.getId().equals(id)).findFirst();
    });
  }

  @Test
  public void addToCart() {
    var req = new ModifyCartRequest();
    req.setItemId(1L);
    req.setQuantity(1);
    req.setUsername(USERNAME);
    var res = controller.addTocart(req);
    var cart = res.getBody();

    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(cart);
    assertEquals(3, cart.getItems().size());
  }

  @Test
  public void addToCartWithFalseItemId() {
    var req = new ModifyCartRequest();
    req.setItemId(3L);
    req.setQuantity(1);
    req.setUsername(USERNAME);
    var res = controller.addTocart(req);

    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  public void addToCartWithFalseUsername() {
    var req = new ModifyCartRequest();
    req.setItemId(1L);
    req.setQuantity(1);
    req.setUsername("falseusername");
    var res = controller.addTocart(req);

    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  public void removeFromCart() {
    var req = new ModifyCartRequest();
    req.setItemId(2L);
    req.setQuantity(1);
    req.setUsername(USERNAME);
    var res = controller.removeFromcart(req);
    var cart = res.getBody();

    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(cart);
    assertEquals(1, cart.getItems().size());
  }

  @Test
  public void removeFromCartWithFalseUsername() {
    var req = new ModifyCartRequest();
    req.setItemId(1L);
    req.setQuantity(1);
    req.setUsername("falseusername");
    var res = controller.removeFromcart(req);

    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  public void removeFromCartWithFalseItemId() {
    var req = new ModifyCartRequest();
    req.setItemId(4L);
    req.setQuantity(1);
    req.setUsername(USERNAME);
    var res = controller.removeFromcart(req);

    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

}
