package com.wpcc.seckillservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SeckillServiceApplication {

  public static void main(
      String[] args) {
    SpringApplication.run(SeckillServiceApplication.class, args);
  }

}
