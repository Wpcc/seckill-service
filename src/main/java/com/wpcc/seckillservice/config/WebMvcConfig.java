package com.wpcc.seckillservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wpcc.seckillservice.auth.JwtAuthenticationInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

  public WebMvcConfig(
      JwtAuthenticationInterceptor jwtAuthenticationInterceptor) {
    this.jwtAuthenticationInterceptor = jwtAuthenticationInterceptor;
  }

  @Override
  public void addInterceptors(
      InterceptorRegistry registry) {
    registry.addInterceptor(jwtAuthenticationInterceptor).addPathPatterns("/api/**")
        .excludePathPatterns("/api/auth/login", "/api/users/register");
  }
}
