package com.wpcc.seckillservice.product;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wpcc.seckillservice.product.dto.ProductResponse;

@Service
public class ProductService {
  private final ProductMapper productMapper;

  public ProductService(
      ProductMapper productMapper) {
    this.productMapper = productMapper;
  }

  public List<ProductResponse> getProducts() {
    List<Product> products = productMapper.findOnSale();
    return products.stream().map(this::toResponse).toList();
  }

  public Optional<ProductResponse> findProductById(
      Long id) {
    return productMapper.findById(id).map(this::toResponse);
  }

  private ProductResponse toResponse(
      Product product) {
    return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
        product.getStock());
  }
}
