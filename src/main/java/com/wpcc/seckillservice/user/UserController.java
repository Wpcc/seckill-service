package com.wpcc.seckillservice.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wpcc.seckillservice.user.dto.RegisterUserRequest;
import com.wpcc.seckillservice.user.dto.RegisterUserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {
  private final UserService userService;

  public UserController(
      UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/users/register")
  public ResponseEntity<RegisterUserResponse> register(
      @Valid @RequestBody
      RegisterUserRequest request) {
    RegisterUserResponse user = userService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

}
