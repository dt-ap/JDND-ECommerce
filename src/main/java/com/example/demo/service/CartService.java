package com.example.demo.service;

import com.example.demo.exception.ItemException;
import com.example.demo.exception.UserException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
public class CartService {
  private static final Logger log = LoggerFactory.getLogger(CartService.class);

  private final CartRepository repository;
  private final ItemRepository itemRepository;
  private final UserRepository userRepository;

  public CartService(CartRepository repository, ItemRepository itemRepository, UserRepository userRepository) {
    this.repository = repository;
    this.itemRepository = itemRepository;
    this.userRepository = userRepository;
  }

  public Cart addToCart(ModifyCartRequest request) throws UserException, ItemException {
    var username = request.getUsername();
    var user = userRepository.findByUsername(username);
    if (user.isEmpty()) {
      var notFound = "Cannot find user '" + username + "'";
      log.debug(notFound);
      throw new UserException(notFound);
    }

    var itemId = request.getItemId();
    var item = itemRepository.findById(itemId);
    if (item.isEmpty()) {
      var notFound = "Cannot find item with id '" + itemId + "'";
      log.debug(notFound);
      throw new ItemException(notFound);
    }

    var cart = user.get().getCart();
    IntStream.range(0, request.getQuantity())
        .forEach(i -> cart.addItem(item.get()));
    repository.save(cart);
    log.info("Successfully added item(s) to '{}' cart", username);
    return cart;
  }

  public Cart removeFromCart(ModifyCartRequest request) throws UserException, ItemException {
    var username = request.getUsername();
    var user = userRepository.findByUsername(username);
    if (user.isEmpty()) {
      var notFound = "Cannot find user '" + username + "'";
      log.debug(notFound);
      throw new UserException(notFound);
    }

    var itemId = request.getItemId();
    var item = itemRepository.findById(itemId);
    if (item.isEmpty()) {
      var notFound = "Cannot find item with id '" + itemId + "'";
      log.debug(notFound);
      throw new ItemException(notFound);
    }

    var cart = user.get().getCart();
    IntStream.range(0, request.getQuantity())
        .forEach(i -> cart.removeItem(item.get()));
    repository.save(cart);
    log.info("Successfully removed item(s) from '{}' cart", username);
    return cart;
  }
}
