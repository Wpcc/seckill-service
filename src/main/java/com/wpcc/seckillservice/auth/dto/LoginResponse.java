package com.wpcc.seckillservice.auth.dto;

import java.time.Instant;

public record LoginResponse(
    Long userId,
    String username,
    String accessToken,
    String tokenType,
    Instant expiresAt) {
}
