package com.wpcc.seckillservice.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.wpcc.seckillservice.product.dto.ProductResponse;

class ProductServiceTest {

  @Test
  void getProducts_shouldMapOnSaleProductsToResponses() {
    ProductMapper productMapper = mock(ProductMapper.class);
    ProductService productService = new ProductService(productMapper);
    Product keyboard = product(1L, "机械键盘", "热插拔键盘", "299.00", 20);
    Product mouse = product(2L, "无线鼠标", "静音鼠标", "99.00", 50);

    when(productMapper.findOnSale()).thenReturn(List.of(keyboard, mouse));

    List<ProductResponse> responses = productService.getProducts();

    assertEquals(2, responses.size());
    assertEquals(1L, responses.getFirst().id());
    assertEquals("机械键盘", responses.getFirst().name());
    assertEquals(new BigDecimal("299.00"), responses.getFirst().price());
  }

  @Test
  void findProductById_shouldMapFoundProductToResponse() {
    ProductMapper productMapper = mock(ProductMapper.class);
    ProductService productService = new ProductService(productMapper);
    Product product = product(1L, "机械键盘", "热插拔键盘", "299.00", 20);

    when(productMapper.findById(1L)).thenReturn(Optional.of(product));

    Optional<ProductResponse> response = productService.findProductById(1L);

    assertTrue(response.isPresent());
    assertEquals(1L, response.orElseThrow().id());
    assertEquals(20, response.orElseThrow().stock());
  }

  @Test
  void findProductById_shouldReturnEmptyWhenProductDoesNotExist() {
    ProductMapper productMapper = mock(ProductMapper.class);
    ProductService productService = new ProductService(productMapper);

    when(productMapper.findById(999L)).thenReturn(Optional.empty());

    assertTrue(productService.findProductById(999L).isEmpty());
  }

  private Product product(Long id, String name, String description, String price, Integer stock) {
    Product product = new Product(name, description, new BigDecimal(price), stock, (byte) 1);
    product.setId(id);
    return product;
  }
}
