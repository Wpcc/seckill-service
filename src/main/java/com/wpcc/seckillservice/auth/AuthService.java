package com.wpcc.seckillservice.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.wpcc.seckillservice.auth.dto.IssuedToken;
import com.wpcc.seckillservice.auth.dto.LoginRequest;
import com.wpcc.seckillservice.auth.dto.LoginResponse;
import com.wpcc.seckillservice.user.User;
import com.wpcc.seckillservice.user.UserMapper;

@Service
public class AuthService {
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
      UserMapper userMapper,
      PasswordEncoder passwordEncoder,
      JwtService jwtService) {
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public LoginResponse login(
      LoginRequest request) {
    User user = userMapper.findByUsername(request.username()).orElseThrow(this::invalidCredentials);

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw invalidCredentials();
    }

    IssuedToken issuedToken = jwtService.createToken(user.getId(), user.getUsername());

    return new LoginResponse(user.getId(), user.getUsername(), issuedToken.accessToken(), "Bearer",
        issuedToken.expiresAt());
  }

  private ResponseStatusException invalidCredentials() {
    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
  }
}
