package com.example.demo.controller;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
  private static final String USERNAME = "username";
  private static final List<Item> ITEMS = List.of(
      Item.of(1L, "item1", BigDecimal.valueOf(3.00), "item1 description"),
      Item.of(2L, "item2", BigDecimal.valueOf(4.00), "item2 description")
  );

  private OrderController controller;
  private final OrderRepository repository = mock(OrderRepository.class);
  private final UserRepository userRepository = mock(UserRepository.class);

  @BeforeEach
  void setup() {
    controller = new OrderController(new OrderService(repository, userRepository));
    var user = new User();
    user.setId(1L);
    user.setUsername(USERNAME);
    user.setPassword("password");

    var cart = new Cart();
    cart.setId(1L);
    cart.setUser(user);
    cart.setItems(ITEMS);
    cart.setTotal(ITEMS.stream().map(Item::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add)); // Price total from items.
    user.setCart(cart);

    var order = UserOrder.createFromCart(cart);
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
    when(repository.findByUser(argThat(u -> u.getId() == user.getId()))).thenReturn(List.of(order));
  }

  @Test
  public void submitOrderByUsername() {
    var res = controller.submit(USERNAME);
    assertNotNull(res);
    assertEquals(HttpStatus.OK, res.getStatusCode());

    var order = res.getBody();
    assertNotNull(order);
    assertEquals(ITEMS.size(), order.getItems().size());
  }

  @Test
  public void submitOrderByFalseUsername() {
    var res = controller.submit("falseusername");
    assertNull(res.getBody());
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }

  @Test
  public void getOrdersByUsername() {
    var submitRes = controller.submit(USERNAME);
    assertNotNull(submitRes.getBody());
    assertEquals(HttpStatus.OK, submitRes.getStatusCode());

    var res = controller.getOrdersForUser(USERNAME);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    var orders = res.getBody();
    assertNotNull(orders);
    assertEquals(1, orders.size());
    assertEquals(submitRes.getBody().getId(), orders.get(0).getId());
  }

  @Test
  public void getOrdersByFalseUsername() {
    var res = controller.getOrdersForUser("falseusername");
    assertNull(res.getBody());
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }
}
