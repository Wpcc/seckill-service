package com.wpcc.seckillservice.product;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wpcc.seckillservice.product.cache.ProductCacheService;
import com.wpcc.seckillservice.product.dto.ProductResponse;

@Service
public class ProductService {
  private final ProductMapper productMapper;
  private final ProductCacheService productCacheService;

  public ProductService(
      ProductMapper productMapper,
      ProductCacheService productCacheService) {
    this.productMapper = productMapper;
    this.productCacheService = productCacheService;
  }

  public List<ProductResponse> getProducts() {
    List<Product> products = productMapper.findOnSale();
    return products.stream().map(this::toResponse).toList();
  }

  public Optional<ProductResponse> findProductById(
      Long id) {
    Optional<ProductResponse> cachedProduct = productCacheService.getById(id);

    if (cachedProduct.isPresent()) {
      return cachedProduct;
    }

    Optional<ProductResponse> databaseProduct = productMapper.findById(id).map(this::toResponse);

    databaseProduct.ifPresent(productCacheService::put);

    return databaseProduct;
  }

  private ProductResponse toResponse(
      Product product) {
    return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
        product.getStock());
  }
}
