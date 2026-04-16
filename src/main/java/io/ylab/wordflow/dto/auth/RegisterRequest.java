package io.ylab.wordflow.dto.auth;

public record RegisterRequest(String username, String password, String role) {}
