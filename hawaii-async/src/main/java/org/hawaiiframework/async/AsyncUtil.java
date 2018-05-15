package org.hawaiiframework.async;

import org.hawaiiframework.async.exception.HawaiiTaskExecutionException;
import org.hawaiiframework.exception.HawaiiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.requireNonNull;

/**
 * Utility class to wrap an asynchronous call and catch all errors.
 */
public final class AsyncUtil {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncUtil.class);

    /**
     * The constructor.
     */
    private AsyncUtil() {
        // Util constructor.
    }

    /**
     * Invoke the body and return the response wrapped as a completable future..
     */
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public static <T> CompletableFuture<T> invoke(final AsyncCallable<T> body) {
        return invoke(false, body);
    }

    /**
     * Invoke the body and return the response wrapped as a completable future..
     */
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public static <T> CompletableFuture<T> invoke(final boolean logError, final AsyncCallable<T> body) {
        final CompletableFuture<T> result = new CompletableFuture<>();

        try {
            LOGGER.trace("Invoking body");
            final T invokeResult = body.invoke();
            LOGGER.trace("Invoking body completed");
            result.complete(invokeResult);
            LOGGER.trace("CompletableFuture completed");
        } catch (Throwable e) {
            if (logError) {
                LOGGER.error("Caught exception.", e);
            }
            result.completeExceptionally(e);
        }
        return result;
    }


    /**
     * Invoke the body and return the response wrapped as a completable future..
     */
    public static CompletableFuture<Void> invoke(final AsyncInvoke body) {
        return invoke(true, () -> {
            body.invoke();
            return new Void();
        });
    }

    /**
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}.
     */
    @SuppressWarnings("rawtypes")
    public static <T> void waitForCompletion(final Long timeout,
            final TimeUnit unit,
            final List<CompletableFuture<T>> futures) {
        waitForCompletion(timeout, unit, futures.toArray(new CompletableFuture[] {}));
    }

    /**
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}.
     */
    public static void waitForCompletion(final Long timeout,
            final TimeUnit unit,
            final CompletableFuture<?>... futures) {
        requireNonNull(futures);
        requireNonNull(timeout);
        requireNonNull(unit);

        waitForCompletion(timeout, unit, CompletableFuture.allOf(futures));
    }

    /**
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}.
     */
    public static void waitForCompletion(final Long timeout,
            final TimeUnit unit,
            final CompletableFuture<?> future) {
        requireNonNull(future);
        requireNonNull(timeout);
        requireNonNull(unit);
        try {
            future.get(timeout, unit);
        } catch (InterruptedException | ExecutionException exception) {
            throw handleException(exception);
        } catch (TimeoutException exception) {
            LOGGER.debug("Stopped waiting after '{} {}' for task(s) to complete or throw an exception.", timeout, unit);
        }
    }

    private static HawaiiException handleException(final Exception exception) {
        if (exception instanceof ExecutionException) {
            return handleExecutionException(exception);
        }

        return new HawaiiTaskExecutionException(exception);
    }

    private static HawaiiException handleExecutionException(final Exception exception) {
        final Throwable cause = exception.getCause();
        if (cause instanceof HawaiiException) {
            return (HawaiiException) cause;
        }
        return new HawaiiTaskExecutionException(cause);
    }

    /**
     * Await completion of the set of completable futures.
     */
    @SuppressWarnings("unchecked")
    public static <X> void awaitCompletion(final List<CompletableFuture<X>>... futureLists)
            throws ExecutionException, InterruptedException {
        if (futureLists == null || futureLists.length == 0) {
            return;
        }
        final List<CompletableFuture<X>> allFutures = new ArrayList<>();
        for (final List<CompletableFuture<X>> futureList : futureLists) {
            allFutures.addAll(futureList);
        }

        final CompletableFuture<?> combinedFuture = CompletableFuture.allOf(allFutures.toArray(new CompletableFuture<?>[] {}));
        combinedFuture.get();
    }
}
