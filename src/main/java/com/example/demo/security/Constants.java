package com.example.demo.security;

public final class Constants {
  public static final String CREATE_USER_URL = "/api/user/create";
  public static final String SECRET_KEY = "ThisKeyShouldBeFromEnv";
  public static final long EXPIRATION_DURATION = 604_800_000; // 7 days
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER = "Authorization";
}
