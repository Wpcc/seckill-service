package com.wpcc.seckillservice.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wpcc.seckillservice.auth.dto.CurrentUserResponse;
import com.wpcc.seckillservice.auth.dto.LoginRequest;
import com.wpcc.seckillservice.auth.dto.LoginResponse;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(
      AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public LoginResponse login(
      @Valid @RequestBody
      LoginRequest request) {
    return authService.login(request);
  }

  @GetMapping("/me")
  public CurrentUserResponse currentUser() {
    return new CurrentUserResponse(CurrentUserContext.requireUserId());
  }

}
