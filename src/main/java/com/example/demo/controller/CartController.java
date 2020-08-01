package com.example.demo.controller;

import com.example.demo.exception.ItemException;
import com.example.demo.exception.UserException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {

  private final CartService service;

  public CartController(CartService service) {
    this.service = service;
  }

  @PostMapping("/addToCart")
  public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
    try {
      return ResponseEntity.ok(service.addToCart(request));
    } catch (UserException | ItemException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/removeFromCart")
  public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
    try {
      return ResponseEntity.ok(service.removeFromCart(request));
    } catch (UserException | ItemException ex) {
      return ResponseEntity.notFound().build();
    }
  }

}
