package com.wpcc.seckillservice.seckill;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SeckillActivityMapper {

  @Select("""
      SELECT id, product_id, seckill_price, seckill_stock,
      start_time, end_time, status
      FROM seckill_activities
      WHERE id = #{id}
            """)
  Optional<SeckillActivity> findById(
      @Param("id")
      Long id);
}
