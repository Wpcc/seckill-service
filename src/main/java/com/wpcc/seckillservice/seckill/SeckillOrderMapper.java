package com.wpcc.seckillservice.seckill;

import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SeckillOrderMapper {
  @Insert("""
      INSERT INTO seckill_orders (
          activity_id,
          user_id,
          product_id,
          seckill_price,
          status
      ) VALUES (
          #{activityId},
          #{userId},
          #{productId},
          #{seckillPrice},
          1
      )
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(
      SeckillOrder order);

  @Select("""
      SELECT
          id,
          activity_id,
          user_id,
          product_id,
          seckill_price,
          status,
          created_at
      FROM seckill_orders
      WHERE activity_id = #{activityId}
        AND user_id = #{userId}
      """)
  Optional<SeckillOrder> findByActivityIdAndUserId(
      @Param("activityId")
      Long activityId,
      @Param("userId")
      Long userId);
}
