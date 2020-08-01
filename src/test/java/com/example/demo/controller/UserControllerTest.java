package com.example.demo.controller;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
  private static final long ID = 1L;
  private static final String USERNAME = "user1";
  private static final String PASSWORD = "pass1";

  private UserController controller;
  private final UserRepository repository = mock(UserRepository.class);
  private final CartRepository cartRepository = mock(CartRepository.class);
  private final BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);

  @BeforeEach
  void setupAll() {
    controller = new UserController(new UserService(repository, cartRepository, passwordEncoder));
    var user = new User();
    user.setId(ID);
    user.setUsername(USERNAME);
    user.setPassword(passwordEncoder.encode(PASSWORD));
    user.setCart(new Cart());
    when(repository.findById(ID)).thenReturn(Optional.of(user));
    when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
  }

  @Test
  public void findUserById() {
    var res = controller.findById(ID);
    assertNotNull(res.getBody());
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(USERNAME, res.getBody().getUsername());
  }

  @Test
  public void findUserByFalseId() {
    var res = controller.findById(2L); // non-existent id
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  public void findUserByUsername() {
    var res = controller.findByUserName(USERNAME);
    assertNotNull(res.getBody());
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(ID, res.getBody().getId());
  }

  @Test
  public void findUserByFalseUsername() {
    var res = controller.findByUserName("falseusername");
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  public void createUser() {
    final var username = "username";
    final var password = "testPassword";
    final var hashedPassword = "testHashedPassword";
    when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

    var req = new CreateUserRequest();
    req.setUsername(username);
    req.setPassword(password);
    req.setPasswordConfirm(password);

    final var res = controller.createUser(req);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    var user = res.getBody();
    assertNotNull(user);
    assertEquals(username, user.getUsername());
    assertEquals(hashedPassword, user.getPassword());
  }

  @Test
  public void createUserWithMismatchPassword() {
    final var username = "testUsername";
    final var password = "testPassword";
    final var hashedPassword = "testHashedPassword";
    when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

    var req = new CreateUserRequest();
    req.setUsername(username);
    req.setPassword(password);
    req.setPasswordConfirm("differentpassword");

    final var res = controller.createUser(req);
    assertNull(res.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
  }


  @Test
  public void createUserWitShortPassword() {
    final var username = "testUsername";
    final var password = "test";
    final var hashedPassword = "testHashedPassword";
    when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

    var req = new CreateUserRequest();
    req.setUsername(username);
    req.setPassword(password);
    req.setPasswordConfirm(password);

    final var res = controller.createUser(req);
    assertNull(res.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
  }

}
