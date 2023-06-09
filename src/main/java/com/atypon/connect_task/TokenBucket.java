package com.atypon.connect_task;

import java.util.concurrent.*;

public class TokenBucket {
  private final int capacity;
  private final Semaphore tokens;
  private final long refillIntervalMillis;

  public TokenBucket(int capacity, long amount, TimeUnit timeUnit) {
    this.capacity = capacity;
    this.tokens = new Semaphore(capacity);
    this.refillIntervalMillis = timeUnit.toMillis(amount);
    scheduleRefill();
  }

  private void scheduleRefill() {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.scheduleAtFixedRate(this::refill, refillIntervalMillis, refillIntervalMillis, TimeUnit.MILLISECONDS);
  }

  private void refill() {
    int currentTokens = tokens.availablePermits();
    int newTokens = capacity - currentTokens;
    tokens.release(newTokens);
  }

  public boolean tryAcquire() {
    return tokens.tryAcquire();
  }
}
