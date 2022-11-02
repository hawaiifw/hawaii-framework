package org.hawaiiframework.async;

import jakarta.validation.constraints.NotNull;
import org.hawaiiframework.async.exception.HawaiiTaskExecutionException;
import org.hawaiiframework.exception.HawaiiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

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
     * Delegates to {@link CompletableFuture#get()}.
     *
     * @param future The completable future to get the value from.
     * @param <T>    The type to return
     * @return the result value
     */
    public static <T> T get(@NotNull final CompletableFuture<T> future) {
        requireNonNull(future);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException exception) {
            throw handleException(exception);
        }
    }

    /**
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}}.
     *
     * @param future  The completable future to get the value from.
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @param <T>     The type to return
     * @return the result value
     */
    public static <T> T get(@NotNull final CompletableFuture<T> future, @NotNull final Long timeout,
            @NotNull final TimeUnit unit) {
        requireNonNull(future);
        requireNonNull(timeout);
        requireNonNull(unit);
        try {
            return future.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            throw handleException(exception);
        }
    }

    /**
     * Delegates to {@link CompletableFuture#getNow(Object)}.
     *
     * @param future        The completable future to get the value from.
     * @param valueIfAbsent The value to return if not completed
     * @param <T>           The type to return
     * @return the result value, if completed, else the given valueIfAbsent
     */
    public static <T> T getNow(@NotNull final CompletableFuture<T> future, final T valueIfAbsent) {
        requireNonNull(future);
        return future.getNow(valueIfAbsent);
    }

    /**
     * Invoke the body and return the response wrapped as a future.
     *
     * @param body the body.
     * @param <T>  the return type
     * @return the response wrapped as a future
     */
    public static <T> CompletableFuture<T> invoke(final AsyncCallable<T> body) {
        return invoke(false, body);
    }

    /**
     * Invoke the body and return the response wrapped as a future.
     *
     * @param logError indicates if errors need to be logged to error.
     * @param body     the body.
     * @param <T>      the return type
     * @return the response wrapped as a future
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
     * Invoke the body and return the response wrapped as a future.
     *
     * @param body the body.
     * @return the response wrapped as a future
     */
    public static CompletableFuture<Void> invoke(final AsyncInvoke body) {
        return invoke(true, () -> {
            body.invoke();
            return new Void();
        });
    }

    /**
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}}.
     *
     * @param timeout the timeout.
     * @param unit    the timeunit.
     * @param futures the list of completable futures.
     * @param <T>     the return type
     */
    @SuppressWarnings("rawtypes")
    public static <T> void waitForCompletion(final Long timeout, final TimeUnit unit, final List<CompletableFuture<T>> futures) {
        waitForCompletion(timeout, unit, futures.toArray(new CompletableFuture[] {}));
    }

    /**
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}}.
     *
     * @param timeout the timeout.
     * @param unit    the timeunit.
     * @param futures the completable futures.
     */
    public static void waitForCompletion(final Long timeout, final TimeUnit unit, final CompletableFuture<?>... futures) {
        requireNonNull(futures);
        requireNonNull(timeout);
        requireNonNull(unit);

        waitForCompletion(timeout, unit, CompletableFuture.allOf(futures));
    }

    /**
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}}.
     *
     * @param timeout the timeout.
     * @param unit    the timeunit.
     * @param future  the completable future.
     */
    public static void waitForCompletion(final Long timeout, final TimeUnit unit, final CompletableFuture<?> future) {
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

    /**
     * Applies the asynchronous {@code function} to each element of {@code inputs}. It then awaits the completion of the
     * calls and returns the function's returns.
     *
     * @param inputs   The collection of input objects.
     * @param function The function to apply
     * @param <I>      The input object's type.
     * @param <T>      The return types.
     * @return A list of type {@code <T>}, or {@code null} if the input is null.
     */
    public static <I, T> List<T> asyncStreamAndMap(final Collection<I> inputs, final Function<I, CompletableFuture<T>> function) {
        return awaitAndGet(asyncMap(inputs, function));
    }

    /**
     * Applies the asynchronous {@code function} to each element of {@code inputs}. It then awaits the completion of the
     * calls and returns the function's returns. Instead of returning a list of lists, this method flap maps the lists' contents onto one
     * result.
     *
     * @param inputs   The collection of input objects.
     * @param function The function to apply
     * @param <I>      The input object's type.
     * @param <T>      The return types.
     * @return A list of type {@code <T>}, or {@code null} if the input is null.
     */
    public static <I, T> List<T> asyncStreamAndMapToSingleList(final Collection<I> inputs,
            final Function<I, CompletableFuture<List<T>>> function) {
        return awaitAndMapToSingleList(asyncMap(inputs, function));
    }

    /**
     * Await the completion of the futures and return the results in one list.
     *
     * @param futures The list of futures.
     * @param <T>     The return types.
     * @return A list of type {@code <T>}, or {@code null} if the input is null.
     */
    public static <T> List<T> awaitAndMapToSingleList(final List<CompletableFuture<List<T>>> futures) {
        return awaitAndGet(futures)
                .stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toList());

    }

    /**
     * Applies the asynchronous {@code function} to each element of {@code inputs}.
     *
     * @param inputs   The collection of input objects.
     * @param function The function to apply
     * @param <I>      The input object's type.
     * @param <T>      The return types.
     * @return A list of completable futures, or {@code null} if the input is null or empty.
     */
    public static <I, T> List<CompletableFuture<T>> asyncMap(final Collection<I> inputs, final Function<I, CompletableFuture<T>> function) {
        if (inputs == null || inputs.isEmpty()) {
            return null;
        }
        return inputs.stream().map(function).collect(Collectors.toList());
    }

    /**
     * Await the completion of all futures and return the futures' answers.
     *
     * @param futures The list of completable futures.
     * @param <T>     The return types.
     * @return The list of the futures' answers.
     */
    public static <T> List<T> awaitAndGet(final List<CompletableFuture<T>> futures) {
        final List<T> results = new ArrayList<>();
        if (futures == null || futures.isEmpty()) {
            return results;
        }

        for (final CompletableFuture<T> future : futures) {
            results.add(get(future));
        }
        return results;
    }

    /**
     * Await completion of the set of futures.
     *
     * @param futures the stream of completable futures.
     * @param <T>     the return type
     * @return the completed list of futures.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<CompletableFuture<T>> awaitCompletion(final List<CompletableFuture<T>> futures) {
        if (futures == null || futures.isEmpty()) {
            return futures;
        }

        final CompletableFuture<?> combinedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[] {}));
        get(combinedFuture);

        return futures;
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
