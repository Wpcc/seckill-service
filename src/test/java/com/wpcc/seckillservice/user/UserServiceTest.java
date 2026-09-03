package com.wpcc.seckillservice.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wpcc.seckillservice.user.dto.RegisterUserRequest;
import com.wpcc.seckillservice.user.dto.RegisterUserResponse;

class UserServiceTest {

  @Test
  void register_shouldEncodePasswordInsertUserAndReturnGeneratedId() {
    UserMapper userMapper = mock(UserMapper.class);
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserService userService = new UserService(userMapper, passwordEncoder);
    RegisterUserRequest request = new RegisterUserRequest("alice", "Passw0rd");

    when(userMapper.findByUsername("alice")).thenReturn(Optional.empty());
    doAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setId(1L);
      return 1;
    }).when(userMapper).insert(any(User.class));

    RegisterUserResponse response = userService.register(request);

    assertEquals(1L, response.id());
    assertEquals("alice", response.username());
    verify(userMapper).insert(org.mockito.ArgumentMatchers.argThat(user -> {
      assertNotEquals("Passw0rd", user.getPasswordHash());
      assertTrue(passwordEncoder.matches("Passw0rd", user.getPasswordHash()));
      return true;
    }));
  }

  @Test
  void register_shouldRejectDuplicateUsernameWithoutEncodingOrInserting() {
    UserMapper userMapper = mock(UserMapper.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    UserService userService = new UserService(userMapper, passwordEncoder);
    RegisterUserRequest request = new RegisterUserRequest("alice", "Passw0rd");

    when(userMapper.findByUsername("alice"))
        .thenReturn(Optional.of(new User("alice", "existing-hash")));

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> userService.register(request));

    assertEquals("用户名已存在", exception.getMessage());
    verifyNoInteractions(passwordEncoder);
    verify(userMapper, never()).insert(any(User.class));
  }

  @Test
  void register_shouldFailWhenInsertDoesNotAffectOneRow() {
    UserMapper userMapper = mock(UserMapper.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    UserService userService = new UserService(userMapper, passwordEncoder);
    RegisterUserRequest request = new RegisterUserRequest("alice", "Passw0rd");

    when(userMapper.findByUsername("alice")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("Passw0rd")).thenReturn("hashed-password");
    when(userMapper.insert(any(User.class))).thenReturn(0);

    IllegalStateException exception = assertThrows(
        IllegalStateException.class,
        () -> userService.register(request));

    assertEquals("用户注册失败", exception.getMessage());
  }
}
