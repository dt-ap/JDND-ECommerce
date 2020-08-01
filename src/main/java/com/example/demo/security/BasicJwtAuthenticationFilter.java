package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BasicJwtAuthenticationFilter extends BasicAuthenticationFilter {
  public BasicJwtAuthenticationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    var header = request.getHeader(Constants.HEADER);

    if (header != null && header.startsWith(Constants.TOKEN_PREFIX)) {
      var subject = JWT.require(Algorithm.HMAC512(Constants.SECRET_KEY)).build()
          .verify(header.replace(Constants.TOKEN_PREFIX, "")).getSubject();
      var authToken = subject != null ? new UsernamePasswordAuthenticationToken(subject, null) : null;
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }
    chain.doFilter(request, response);
  }
}
