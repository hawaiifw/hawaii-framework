/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawaiiframework.async;

import org.hawaiiframework.async.exception.HawaiiTaskExecutionException;
import org.hawaiiframework.exception.HawaiiException;

import javax.validation.constraints.NotNull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.requireNonNull;

/**
 * Utility to retrieve the value from a {@link CompletableFuture}.
 * <p>
 * All exceptions are wrapped in a {@link HawaiiTaskExecutionException}.
 */
public final class HawaiiAsyncUtil {

    private HawaiiAsyncUtil() {
        // Private utility constructor.
    }

    /**
     * Delegates to {@link CompletableFuture#get()}.
     */
    public static <T> T get(@NotNull final CompletableFuture<T> future) {
        requireNonNull(future);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw handleException(e);
        }
    }

    /**
     * Delegates to {@link CompletableFuture#get(long, TimeUnit)}}.
     */
    public static <T> T get(@NotNull final CompletableFuture<T> future, @NotNull final Long timeout,
            @NotNull final TimeUnit unit) {
        requireNonNull(future);
        requireNonNull(timeout);
        requireNonNull(unit);
        try {
            return future.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw handleException(e);
        }
    }

    private static HawaiiException handleException(final Exception e) {
        if (e instanceof ExecutionException) {
            final Throwable cause = e.getCause();
            if (cause instanceof HawaiiException) {
                return (HawaiiException) cause;
            }
            return new HawaiiTaskExecutionException(cause);
        }

        return new HawaiiTaskExecutionException(e);
    }

    /**
     * Delegates to {@link CompletableFuture#getNow(Object)}.
     */
    public static <T> T getNow(@NotNull final CompletableFuture<T> future, final T valueIfAbsent) {
        requireNonNull(future);
        return future.getNow(valueIfAbsent);
    }

}
