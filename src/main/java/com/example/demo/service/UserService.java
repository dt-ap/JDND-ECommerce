package com.example.demo.service;

import com.example.demo.exception.UserException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository repository;
  private final CartRepository cartRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository repository, CartRepository cartRepository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.cartRepository = cartRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Optional<User> getById(Long id) {
    return repository.findById(id);
  }

  public Optional<User> getByUsername(String username) {
    return repository.findByUsername(username);
  }

  public User createUser(CreateUserRequest request) throws UserException {
    var username = request.getUsername();
    var password = request.getPassword();
    if (password == null || password.length() <= 7 || !password.equals(request.getPasswordConfirm())) {
      log.error("Invalid password. Failed to create user '{}'", username);
      throw new UserException("Invalid password.");
    }

    var cart = new Cart();
    cartRepository.save(cart);

    var user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setCart(cart);
    repository.save(user);

    log.info("User '{}' created", username);
    return user;
  }
}
