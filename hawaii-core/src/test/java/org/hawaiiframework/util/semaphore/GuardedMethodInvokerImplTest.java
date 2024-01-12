package org.hawaiiframework.util.semaphore;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link GuardedMethodInvokerImpl}.
 *
 * @author Rutger Lubbers
 */
public class GuardedMethodInvokerImplTest {

  private GuardedMethodInvoker invoker;
  private GuardedMethodInvoker invoker2;

  private AtomicBoolean method1Invoked;
  private AtomicBoolean method2Invoked;

  @Before
  public void setup() {
    invoker = new GuardedMethodInvokerImpl();
    invoker2 = new GuardedMethodInvokerImpl();

    method1Invoked = new AtomicBoolean(false);
    method2Invoked = new AtomicBoolean(false);
  }

  @Test
  public void methodsCanBeInvokedInSequence() {
    invoker.invokeIfNotActive(() -> toggleAndSleep(method1Invoked, 1L));
    invoker.invokeIfNotActive(() -> toggleAndSleep(method2Invoked, 1L));

    assertTrue(method1Invoked.get());
    assertTrue(method2Invoked.get());
  }

  @Test
  public void methodsCannotBeInvokedInParallel() throws InterruptedException {
    CountDownLatch startLatch = new CountDownLatch(1);

    CountDownLatch stopLatch = new CountDownLatch(2);

    Thread thread1 =
        new Thread(
            () ->
                invoker.invokeIfNotActive(
                    () -> {
                      startLatch.countDown();
                      toggleAndSleep(method1Invoked, 30L);
                      stopLatch.countDown();
                    }));
    Thread thread2 =
        new Thread(
            () ->
                invoker.invokeIfNotActive(
                    () -> {
                      toggleAndSleep(method2Invoked, 30L);
                      stopLatch.countDown();
                    }));
    thread1.start();

    startLatch.await(1, SECONDS);
    thread2.start();

    stopLatch.await(1, SECONDS);
    assertTrue(method1Invoked.get());
    assertFalse(method2Invoked.get());
  }

  @Test
  public void multipleGuardedInvokersCanBeInvokedInParallel() throws InterruptedException {
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch stopLatch = new CountDownLatch(2);

    Thread thread1 =
        new Thread(
            () ->
                invoker.invokeIfNotActive(
                    () -> {
                      startLatch.countDown();
                      toggleAndSleep(method1Invoked, 30L);
                      stopLatch.countDown();
                    }));
    Thread thread2 =
        new Thread(
            () ->
                invoker2.invokeIfNotActive(
                    () -> {
                      toggleAndSleep(method2Invoked, 30L);
                      stopLatch.countDown();
                    }));
    thread1.start();

    startLatch.await(1, SECONDS);
    thread2.start();

    stopLatch.await(1, SECONDS);
    assertTrue(method1Invoked.get());
    assertTrue(method2Invoked.get());
  }

  private static void toggleAndSleep(AtomicBoolean method, long sleep) {
    method.getAndSet(true);
    try {
      Thread.sleep(sleep);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
