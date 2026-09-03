package com.wpcc.seckillservice.user;

import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
  @Select("""
      SELECT id,username,password_hash,created_at
      FROM users
      WHERE username = #{username}
      """)
  Optional<User> findByUsername(
      @Param("username")
      String username);

  @Insert("""
      INSERT INTO users (username,password_hash)
      VALUES (#{username},#{passwordHash})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(
      User user);

}