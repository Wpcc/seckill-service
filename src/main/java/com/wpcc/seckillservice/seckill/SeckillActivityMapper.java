package com.wpcc.seckillservice.seckill;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

  @Update("""
      UPDATE seckill_activities
      SET seckill_stock = seckill_stock - 1
      WHERE id = #{activityId}
        AND status = 1
        AND start_time <= NOW()
        AND end_time > NOW()
        AND seckill_stock > 0
        """)
  int decreaseStockIfAvailable(
      @Param("activityId")
      Long activityId);
}
