package com.wpcc.seckillservice.auth;

public final class CurrentUserContext {
  private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

  private CurrentUserContext() {
  }

  public static void setUserId(
      Long userId) {
    CURRENT_USER_ID.set(userId);
  }

  public static Long requireUserId() {
    Long userId = CURRENT_USER_ID.get();

    if (userId == null) {
      throw new IllegalStateException("当前请求没有登录用户");
    }

    return userId;
  }

  public static void clear() {
    CURRENT_USER_ID.remove();
  }

}
