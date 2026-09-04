package com.wpcc.seckillservice.product;

import java.math.BigDecimal;

public class Product {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer stock;
  private Byte status;

  public Product() {

  }

  public Product(
      String name,
      String description,
      BigDecimal price,
      Integer stock,
      Byte status) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
    this.status = status;
  }

  public void setId(
      Long id) {
    this.id = id;
  }

  public void setName(
      String name) {
    this.name = name;
  }

  public void setDescription(
      String description) {
    this.description = description;
  }

  public void setPrice(
      BigDecimal price) {
    this.price = price;
  }

  public void setStock(
      Integer stock) {
    this.stock = stock;
  }

  public void setStatus(
      Byte status) {
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Integer getStock() {
    return stock;
  }

  public Byte getStatus() {
    return status;
  }

}
