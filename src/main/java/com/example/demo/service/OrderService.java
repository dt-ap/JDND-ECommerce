package com.example.demo.service;

import com.example.demo.exception.UserException;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
  private static final Logger log = LoggerFactory.getLogger(OrderService.class);

  private final OrderRepository repository;
  private final UserRepository userRepository;

  public OrderService(OrderRepository repository, UserRepository userRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
  }

  public UserOrder submitByUsername(String username) throws UserException {
    var user = userRepository.findByUsername(username);
    if (user.isEmpty()) {
      var notFound = "Cannot find user '" + username + "'";
      log.debug(notFound);
      throw new UserException(notFound);
    }

    var order = UserOrder.createFromCart(user.get().getCart());
    repository.save(order);
    log.info("Successfully create order for '{}'", username);
    return order;
  }

  public List<UserOrder> getManyByUsername(String username) throws UserException {
    var user = userRepository.findByUsername(username);
    if (user.isEmpty()) {
      var notFound = "Cannot find user '" + username + "'";
      log.debug(notFound);
      throw new UserException(notFound);
    }
    var orders = repository.findByUser(user.get());
    log.info("User '{}' have {} order(s)", username, orders.size());
    return orders;
  }
}
