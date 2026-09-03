package com.wpcc.seckillservice.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.wpcc.seckillservice.auth.dto.IssuedToken;
import com.wpcc.seckillservice.auth.dto.LoginRequest;
import com.wpcc.seckillservice.auth.dto.LoginResponse;
import com.wpcc.seckillservice.user.User;
import com.wpcc.seckillservice.user.UserMapper;

class AuthServiceTest {

  @Test
  void login_shouldVerifyPasswordAndReturnIssuedToken() {
    UserMapper userMapper = mock(UserMapper.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    JwtService jwtService = mock(JwtService.class);
    AuthService authService = new AuthService(userMapper, passwordEncoder, jwtService);
    User user = new User("alice", "stored-hash");
    user.setId(1001L);
    Instant expiresAt = Instant.now().plusSeconds(3600);

    when(userMapper.findByUsername("alice")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("Passw0rd", "stored-hash")).thenReturn(true);
    when(jwtService.createToken(1001L, "alice"))
        .thenReturn(new IssuedToken("signed-token", expiresAt));

    LoginResponse response = authService.login(new LoginRequest("alice", "Passw0rd"));

    assertEquals(1001L, response.userId());
    assertEquals("alice", response.username());
    assertEquals("signed-token", response.accessToken());
    assertEquals("Bearer", response.tokenType());
    assertEquals(expiresAt, response.expiresAt());
    verify(jwtService).createToken(1001L, "alice");
  }

  @Test
  void login_shouldReturnUnauthorizedWhenPasswordDoesNotMatch() {
    UserMapper userMapper = mock(UserMapper.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    JwtService jwtService = mock(JwtService.class);
    AuthService authService = new AuthService(userMapper, passwordEncoder, jwtService);
    User user = new User("alice", "stored-hash");
    user.setId(1001L);

    when(userMapper.findByUsername("alice")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong-password", "stored-hash")).thenReturn(false);

    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> authService.login(new LoginRequest("alice", "wrong-password")));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("用户名或密码错误", exception.getReason());
    verifyNoInteractions(jwtService);
  }
}
