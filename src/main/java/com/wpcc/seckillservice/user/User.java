package com.wpcc.seckillservice.user;

public class User {
  private Long id;
  private String username;
  private String passwordHash;

  public User() {
  }

  public User(
      String username,
      String passwordHash) {
    this.username = username;
    this.passwordHash = passwordHash;
  }

  public Long getId() {
    return id;
  }

  public void setId(
      Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

}
