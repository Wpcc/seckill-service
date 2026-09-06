package com.wpcc.seckillservice.product.cache;

import java.util.Optional;

import com.wpcc.seckillservice.product.dto.ProductResponse;

public record ProductCacheResult(
    Optional<ProductResponse> product,
    boolean fromCache) {

}
