package com.wpcc.seckillservice.product;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper {
  @Select("""
      SELECT id,name,description,price,stock
      FROM products
      WHERE id = #{id}
      AND status = 1
      ORDER BY id DESC
      """)

  Optional<Product> findById(
      @Param("id")
      Long id);

  @Select("""
      SELECT id,name,description,price,stock,status
      FROM products
      WHERE status = 1
      ORDER BY id DESC
      """)
  List<Product> findOnSale();
}
