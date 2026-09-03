package com.wpcc.seckillservice.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wpcc.seckillservice.user.dto.RegisterUserRequest;
import com.wpcc.seckillservice.user.dto.RegisterUserResponse;

@Service
public class UserService {
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserService(
      UserMapper userMapper,
      PasswordEncoder passwordEncoder) {
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  public RegisterUserResponse register(
      RegisterUserRequest request) {
    if (userMapper.findByUsername(request.username()).isPresent()) {
      throw new IllegalArgumentException("用户名已存在");
    }

    String passwordHash = passwordEncoder.encode(request.password());

    User user = new User(request.username(), passwordHash);

    int affectedRows = userMapper.insert(user);

    if (affectedRows != 1) {
      throw new IllegalStateException("用户注册失败");
    }

    return new RegisterUserResponse(user.getId(), user.getUsername());
  }
}
