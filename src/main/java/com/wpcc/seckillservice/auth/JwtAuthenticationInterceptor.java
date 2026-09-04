package com.wpcc.seckillservice.auth;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
  private final JwtService jwtService;

  public JwtAuthenticationInterceptor(
      JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler) throws IOException {
    String authorization = request.getHeader("Authorization");

    if (authorization == null || !authorization.startsWith("Bearer ")) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少或无效的登录令牌");
      return false;
    }

    String token = authorization.substring(7).trim();

    if (token.isEmpty()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少或无效的登录令牌");
      return false;
    }

    try {
      jwtService.parseUserId(token);
      return true;
    } catch (IllegalArgumentException exception) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效或已过期的登录令牌");
      return false;
    }
  }
}
