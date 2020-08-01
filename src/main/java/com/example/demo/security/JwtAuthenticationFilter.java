package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authManager;

  public JwtAuthenticationFilter(AuthenticationManager authManager) {
    this.authManager = authManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try {
      var userReq = new ObjectMapper().readValue(request.getInputStream(), CreateUserRequest.class);
      return authManager.authenticate(new UsernamePasswordAuthenticationToken(userReq.getUsername(), userReq.getPassword(), Collections.emptyList()));
    } catch (IOException ex) {
      throw new JwtAuthenticationException(ex.getMessage(), ex);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
    var expirationAt = new Date(System.currentTimeMillis() + Constants.EXPIRATION_DURATION);
    var token = JWT.create().withSubject(authResult.getName())
        .withExpiresAt(expirationAt)
        .sign(Algorithm.HMAC512(Constants.SECRET_KEY));

    response.addHeader(Constants.HEADER, Constants.TOKEN_PREFIX + token);
  }
}
