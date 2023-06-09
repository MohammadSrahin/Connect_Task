package com.atypon.connect_task;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Throttler {

  protected static final int DELAY = 3;
  private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, AtomicInteger> queuedRequests = new ConcurrentHashMap<>();

  public boolean canAccess(String ip, String path) {
    if (path.equals("/api/posts")) {
      return true;
    }

    int maxRequests = path.equals("/api/profile") ? 10 : 30;

    String key = ip + ":" + path;
    TokenBucket tokenBucket = buckets.computeIfAbsent(key,
        v -> new TokenBucket(maxRequests, 1, TimeUnit.MINUTES));

    AtomicInteger queuedRequestsCount = queuedRequests.computeIfAbsent(key,
        v -> new AtomicInteger(0));

    if (queuedRequestsCount.get() >= maxRequests && path.equals("/api/history")) {
      return false;
    }

    if (!tokenBucket.tryAcquire()) {
      try {
        queuedRequestsCount.incrementAndGet();
        TimeUnit.SECONDS.sleep(DELAY);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        queuedRequestsCount.decrementAndGet();
      }
    }
    return true;
  }
}