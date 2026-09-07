package com.wpcc.seckillservice.seckill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SeckillOrder {
  private Long id;
  private Long activityId;
  private Long userId;
  private Long productId;
  private BigDecimal seckillPrice;
  private Integer status;
  private LocalDateTime createdAt;

  public SeckillOrder() {

  }

  public SeckillOrder(
      Long activityId,
      Long userId,
      Long productId,
      BigDecimal seckillPrice) {
    this.activityId = activityId;
    this.userId = userId;
    this.productId = productId;
    this.seckillPrice = seckillPrice;
  }

  public SeckillOrder(
      Long activityId,
      Long userId,
      Long productId,
      BigDecimal seckillPrice,
      Integer status,
      LocalDateTime createdAt) {
    this.activityId = activityId;
    this.userId = userId;
    this.productId = productId;
    this.seckillPrice = seckillPrice;
    this.status = status;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(
      Long id) {
    this.id = id;
  }

  public Long getActivityId() {
    return activityId;
  }

  public void setActivityId(
      Long activityId) {
    this.activityId = activityId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(
      Long userId) {
    this.userId = userId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(
      Long productId) {
    this.productId = productId;
  }

  public BigDecimal getSeckillPrice() {
    return seckillPrice;
  }

  public void setSeckillPrice(
      BigDecimal seckillPrice) {
    this.seckillPrice = seckillPrice;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(
      Integer status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(
      LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

}