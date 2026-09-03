package com.wpcc.seckillservice.auth.dto;

import java.time.Instant;

public record IssuedToken(
    String accessToken,
    Instant expiresAt) {

}
