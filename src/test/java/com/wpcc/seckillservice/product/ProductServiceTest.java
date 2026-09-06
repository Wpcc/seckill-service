package com.wpcc.seckillservice.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.wpcc.seckillservice.product.cache.ProductCacheResult;
import com.wpcc.seckillservice.product.cache.ProductCacheService;
import com.wpcc.seckillservice.product.dto.ProductResponse;

class ProductServiceTest {

  @Test
  void getProducts_shouldMapOnSaleProductsToResponses() {
    ProductMapper productMapper = mock(ProductMapper.class);
    ProductCacheService productCacheService = mock(ProductCacheService.class);
    ProductService productService = new ProductService(productMapper, productCacheService);
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
  void findProductById_shouldReturnCachedProductWithoutQueryingDatabase() {
    ProductMapper productMapper = mock(ProductMapper.class);
    ProductCacheService productCacheService = mock(ProductCacheService.class);
    ProductService productService = new ProductService(productMapper, productCacheService);
    ProductResponse cachedProduct = new ProductResponse(1L, "机械键盘", "热插拔键盘", new BigDecimal("299.00"), 20);

    when(productCacheService.getById(1L))
        .thenReturn(new ProductCacheResult(Optional.of(cachedProduct), true));

    Optional<ProductResponse> response = productService.findProductById(1L);

    assertTrue(response.isPresent());
    assertEquals(1L, response.orElseThrow().id());
    assertEquals(20, response.orElseThrow().stock());
    verify(productMapper, never()).findById(1L);
  }

  @Test
  void findProductById_shouldQueryDatabaseAndCacheProductOnCacheMiss() {
    ProductMapper productMapper = mock(ProductMapper.class);
    ProductCacheService productCacheService = mock(ProductCacheService.class);
    ProductService productService = new ProductService(productMapper, productCacheService);
    Product product = product(1L, "机械键盘", "热插拔键盘", "299.00", 20);

    when(productCacheService.getById(1L))
        .thenReturn(new ProductCacheResult(Optional.empty(), false));
    when(productMapper.findById(1L)).thenReturn(Optional.of(product));

    Optional<ProductResponse> response = productService.findProductById(1L);

    assertTrue(response.isPresent());
    verify(productCacheService).put(response.orElseThrow());
  }

  @Test
  void findProductById_shouldReturnEmptyWhenProductDoesNotExist() {
    ProductMapper productMapper = mock(ProductMapper.class);
    ProductCacheService productCacheService = mock(ProductCacheService.class);
    ProductService productService = new ProductService(productMapper, productCacheService);

    when(productCacheService.getById(999L))
        .thenReturn(new ProductCacheResult(Optional.empty(), false));
    when(productMapper.findById(999L)).thenReturn(Optional.empty());

    assertTrue(productService.findProductById(999L).isEmpty());
    verify(productCacheService, never()).put(org.mockito.ArgumentMatchers.any());
    verify(productCacheService).putNotFound(999L);
  }

  @Test
  void findProductById_shouldReturnEmptyWithoutQueryingDatabaseWhenNullValueIsCached() {
    ProductMapper productMapper = mock(ProductMapper.class);
    ProductCacheService productCacheService = mock(ProductCacheService.class);
    ProductService productService = new ProductService(productMapper, productCacheService);

    when(productCacheService.getById(999L))
        .thenReturn(new ProductCacheResult(Optional.empty(), true));

    assertTrue(productService.findProductById(999L).isEmpty());
    verify(productMapper, never()).findById(999L);
  }

  private Product product(Long id, String name, String description, String price, Integer stock) {
    Product product = new Product(name, description, new BigDecimal(price), stock, (byte) 1);
    product.setId(id);
    return product;
  }
}
