package com.example.demo.controller;

import com.example.demo.exception.UserException;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

  private final OrderService service;

  public OrderController(OrderService service) {
    this.service = service;
  }

  @PostMapping("/submit/{username}")
  public ResponseEntity<UserOrder> submit(@PathVariable String username) {
    try {
      return ResponseEntity.ok(service.submitByUsername(username));
    } catch (UserException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/history/{username}")
  public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
    try {
      return ResponseEntity.ok(service.getManyByUsername(username));
    } catch (UserException ex) {
      return ResponseEntity.notFound().build();
    }
  }
}
