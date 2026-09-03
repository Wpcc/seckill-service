package com.wpcc.seckillservice.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.wpcc.seckillservice.auth.dto.IssuedToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private final JwtProperties jwtProperties;
  private final SecretKey signingKey;

  public JwtService(
      JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;

    byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public IssuedToken createToken(
      Long userId,
      String username) {
    Instant now = Instant.now();

    Instant expiresAt = now.plus(jwtProperties.expireMinutes(), ChronoUnit.MINUTES);

    String accessToken = Jwts.builder().subject(String.valueOf(userId)).claim("username", username)
        .issuedAt(Date.from(now)).expiration(Date.from(expiresAt)).signWith(signingKey).compact();

    return new IssuedToken(accessToken, expiresAt);
  }

  public Long parseUserId(
      String token) {
    try {
      Claims claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();

      return Long.valueOf(claims.getSubject());
    } catch (JwtException | IllegalArgumentException exception) {
      throw new IllegalArgumentException("无效或已过期的登录令牌");
    }
  }
}
