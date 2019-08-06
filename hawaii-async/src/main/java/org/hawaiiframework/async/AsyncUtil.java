package org.hawaiiframework.async;

import org.hawaiiframework.async.exception.HawaiiTaskExecutionException;
import org.hawaiiframework.exception.HawaiiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
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
    public static CompletableFuture<VoidResult> invokeAndLogError(final AsyncInvoke body) {
        return invoke(true, () -> {
            body.invoke();
            return new VoidResult();
        });
    }

    /**
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}}.
     */
    @SuppressWarnings("rawtypes")
    public static <T> void expectErrorInTimeoutOrStopWaiting(@NotNull final Long timeout,
            @NotNull final TimeUnit unit,
            @NotNull final List<CompletableFuture<T>> futures) {
        expectErrorInTimeoutOrStopWaiting(timeout, unit, futures.toArray(new CompletableFuture[0]));
    }

    /**
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}}.
     */
    public static void expectErrorInTimeoutOrStopWaiting(@NotNull final Long timeout,
            @NotNull final TimeUnit unit,
            @NotNull final CompletableFuture<?>... futures) {
        requireNonNull(futures);
        requireNonNull(timeout);
        requireNonNull(unit);

        expectErrorInTimeoutOrStopWaiting(timeout, unit, CompletableFuture.allOf(futures));
    }

    /**
     * This call waits the provided timeout for an error to occur. After the timeout, this call continues with the flow without waiting
     * for the actual result. This is to prevent waiting for backend calls to return, while we are not interested in the return value
     * for the current flow. (e.g. send mail via BSL). The assumption is that if no error is returned within the timeout, the call will be
     * successful.
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}}.
     */
    public static void expectErrorInTimeoutOrStopWaiting(@NotNull final Long timeout,
            @NotNull final TimeUnit unit,
            @NotNull final CompletableFuture<?> future) {
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
}
