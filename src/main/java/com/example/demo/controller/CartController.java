package com.example.demo.controller;

import com.example.demo.exception.ItemException;
import com.example.demo.exception.UserException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

  private final CartService service;

  public CartController(CartService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
    try {
      return ResponseEntity.ok(service.addToCart(request));
    } catch (UserException | ItemException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping
  public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
    try {
      return ResponseEntity.ok(service.removeFromCart(request));
    } catch (UserException | ItemException ex) {
      return ResponseEntity.notFound().build();
    }
  }

}
