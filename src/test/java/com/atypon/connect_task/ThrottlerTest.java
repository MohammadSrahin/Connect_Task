package com.atypon.connect_task;

import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;

class ThrottlerTest {
  private Throttler throttler;
  private ExecutorService executorService;
  private final int numThreads = 70;

  @BeforeEach
  void setUp() {
    throttler = new Throttler();
    executorService = Executors.newFixedThreadPool(numThreads);
  }

  @Test
  void testOpenAccess() throws InterruptedException {
    String ip = "192.168.0.1";
    String path = "/api/posts";
    CountDownLatch startLatch = new CountDownLatch(1);

    for (int i = 0; i < numThreads; i++) {
      int requestNum = i + 1;
      executorService.submit(() -> {
        try {
          startLatch.await();
          boolean canAccess = throttler.canAccess(ip, path);
          System.out.println("Request to " + path + " #" + requestNum + " canAccess: " + canAccess);
          assertTrue(canAccess);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
    }

    startLatch.countDown();
  }

  @Test
  void testProfileThrottling() throws InterruptedException {
    String ip = "192.168.0.2";
    String path = "/api/profile";
    CountDownLatch startLatch = new CountDownLatch(1);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);
    AtomicInteger requestCount = new AtomicInteger(0);

    for (int i = 0; i < numThreads; i++) {
      executorService.submit(() -> {
        try {
          startLatch.await();
          long startTime = System.currentTimeMillis();
          boolean canAccess = throttler.canAccess(ip, path);
          long endTime = System.currentTimeMillis();
          long duration = endTime - startTime;

          int requestNumber = requestCount.incrementAndGet();
          System.out.println("Request to " + path + " #" + requestNumber + " canAccess: " + canAccess);
          if (canAccess) {
            successCount.incrementAndGet();
            if (requestNumber > 10) {
              assertTrue(duration >= TimeUnit.SECONDS.toMillis(Throttler.DELAY));
            }
          } else {
            failureCount.incrementAndGet();
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
    }

    startLatch.countDown();

    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(2, TimeUnit.MINUTES)) {
        List<Runnable> notExecutedTasks = executorService.shutdownNow();
        System.out.println("Failed to execute all the tasks: " + notExecutedTasks.size());
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }

    System.out.println("Success count: " + successCount.get());
    System.out.println("Failure count: " + failureCount.get());
    assertEquals(70, successCount.get());
  }

  @Test
  void testHistoryThrottling() throws InterruptedException {
    String ip = "192.168.0.3";
    String path = "/api/history";
    CountDownLatch startLatch = new CountDownLatch(1);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);
    AtomicInteger requestCount = new AtomicInteger(0);

    for (int i = 0; i < numThreads; i++) {
      executorService.submit(() -> {
        try {
          startLatch.await();
          long startTime = System.currentTimeMillis();
          boolean canAccess = throttler.canAccess(ip, path);
          long endTime = System.currentTimeMillis();
          long duration = endTime - startTime;

          int requestNumber = requestCount.incrementAndGet();
          System.out.println("Request to " + path + " #" + requestNumber + " canAccess: " + canAccess);
          if (canAccess) {
            successCount.incrementAndGet();
            if (requestNumber > 30) {
              assertTrue(duration >= TimeUnit.SECONDS.toMillis(3));
            }
          } else {
            failureCount.incrementAndGet();
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
    }

    startLatch.countDown();

    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(2, TimeUnit.MINUTES)) {
        List<Runnable> notExecutedTasks = executorService.shutdownNow();
        System.out.println("Failed to execute all the tasks: " + notExecutedTasks.size());
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }

    System.out.println("Success count: " + successCount.get());
    System.out.println("Failure count: " + failureCount.get());
    assertEquals(60, successCount.get());
  }
}
