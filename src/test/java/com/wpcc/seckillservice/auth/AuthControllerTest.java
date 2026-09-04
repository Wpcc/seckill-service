package com.wpcc.seckillservice.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.wpcc.seckillservice.auth.dto.CurrentUserResponse;

class AuthControllerTest {

  @AfterEach
  void clearCurrentUser() {
    CurrentUserContext.clear();
  }

  @Test
  void currentUser_shouldReturnUserIdFromCurrentUserContext() {
    AuthController controller = new AuthController(mock(AuthService.class));
    CurrentUserContext.setUserId(1001L);

    CurrentUserResponse response = controller.currentUser();

    assertEquals(1001L, response.userId());
  }

  @Test
  void currentUser_shouldFailWhenCurrentUserContextIsMissing() {
    AuthController controller = new AuthController(mock(AuthService.class));

    IllegalStateException exception = assertThrows(IllegalStateException.class, controller::currentUser);

    assertEquals("当前请求没有登录用户", exception.getMessage());
  }
}
