package com.wpcc.seckillservice.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import com.wpcc.seckillservice.auth.dto.IssuedToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

  private static final String TEST_SECRET = Base64.getEncoder()
      .encodeToString("01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));

  @Test
  void createToken_shouldContainUserIdAndUsernameAndBeParseable() {
    JwtService jwtService = new JwtService(new JwtProperties(TEST_SECRET, 60));

    IssuedToken issuedToken = jwtService.createToken(1001L, "alice");

    assertEquals(1001L, jwtService.parseUserId(issuedToken.accessToken()));
    assertTrue(issuedToken.expiresAt().isAfter(Instant.now()));

    SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
    Claims claims = Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(issuedToken.accessToken())
        .getPayload();

    assertEquals("1001", claims.getSubject());
    assertEquals("alice", claims.get("username", String.class));
  }

  @Test
  void parseUserId_shouldRejectTamperedToken() {
    JwtService jwtService = new JwtService(new JwtProperties(TEST_SECRET, 60));
    String token = jwtService.createToken(1001L, "alice").accessToken();

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> jwtService.parseUserId(token + "tampered"));

    assertEquals("无效或已过期的登录令牌", exception.getMessage());
  }
}
