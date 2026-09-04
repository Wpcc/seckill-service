package com.wpcc.seckillservice.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JwtAuthenticationInterceptorTest {

  @Test
  void preHandle_shouldRejectRequestWithoutAuthorizationHeader() throws Exception {
    JwtService jwtService = mock(JwtService.class);
    JwtAuthenticationInterceptor interceptor = new JwtAuthenticationInterceptor(jwtService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertFalse(allowed);
    org.junit.jupiter.api.Assertions.assertEquals(401, response.getStatus());
    verifyNoInteractions(jwtService);
  }

  @Test
  void preHandle_shouldRejectEmptyBearerToken() throws Exception {
    JwtService jwtService = mock(JwtService.class);
    JwtAuthenticationInterceptor interceptor = new JwtAuthenticationInterceptor(jwtService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addHeader("Authorization", "Bearer   ");

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertFalse(allowed);
    org.junit.jupiter.api.Assertions.assertEquals(401, response.getStatus());
    verifyNoInteractions(jwtService);
  }

  @Test
  void preHandle_shouldRejectInvalidToken() throws Exception {
    JwtService jwtService = mock(JwtService.class);
    JwtAuthenticationInterceptor interceptor = new JwtAuthenticationInterceptor(jwtService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addHeader("Authorization", "Bearer invalid-token");
    doThrow(new IllegalArgumentException("无效或已过期的登录令牌"))
        .when(jwtService).parseUserId("invalid-token");

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertFalse(allowed);
    org.junit.jupiter.api.Assertions.assertEquals(401, response.getStatus());
    verify(jwtService).parseUserId("invalid-token");
  }

  @Test
  void preHandle_shouldAllowRequestWithValidBearerToken() throws Exception {
    JwtService jwtService = mock(JwtService.class);
    JwtAuthenticationInterceptor interceptor = new JwtAuthenticationInterceptor(jwtService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addHeader("Authorization", "Bearer valid-token");
    when(jwtService.parseUserId("valid-token")).thenReturn(1001L);

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertTrue(allowed);
    verify(jwtService).parseUserId("valid-token");
  }
}
