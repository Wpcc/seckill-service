package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

class SeckillStockConcurrencyDemoTest {

  @Test
  void naiveCheckThenDecrease_canOversell() throws Exception {
    UnsafeStock stock = new UnsafeStock(1, new CyclicBarrier(2));
    ExecutorService executor = Executors.newFixedThreadPool(2);

    try {
      Future<Boolean> first = executor.submit(stock::tryDecrease);
      Future<Boolean> second = executor.submit(stock::tryDecrease);

      assertTrue(first.get());
      assertTrue(second.get());
      assertEquals(-1, stock.getStock());
    } finally {
      executor.shutdownNow();
    }
  }

  /**
   * 仅用于稳定复现竞态条件。
   *
   * <p>两个线程都会先读到 stock = 1，再由 barrier 放行执行扣减，因此两个请求都“成功”，库存变为 -1。
   * 真实秒杀服务不使用这种先查后扣写法，而是使用数据库的条件 UPDATE。</p>
   */
  private static class UnsafeStock {
    private int stock;
    private final CyclicBarrier barrier;

    private UnsafeStock(int stock, CyclicBarrier barrier) {
      this.stock = stock;
      this.barrier = barrier;
    }

    boolean tryDecrease() {
      if (stock <= 0) {
        return false;
      }

      try {
        barrier.await();
      } catch (Exception exception) {
        throw new IllegalStateException("并发演示线程同步失败", exception);
      }

      stock--;
      return true;
    }

    int getStock() {
      return stock;
    }
  }
}
