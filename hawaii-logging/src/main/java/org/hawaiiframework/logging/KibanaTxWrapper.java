/*
 * Copyright 2015-2020 the original author or authors.
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

package org.hawaiiframework.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * Helper to start a hawaii transaction, for logging purposes.
 */
public final class KibanaTxWrapper {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KibanaTxWrapper.class);

    /**
     * Utility constructor.
     */
    private KibanaTxWrapper() {
        // Do nothing.
    }

    /**
     * Wrap the call with a kibana transaction.
     *
     * @param endPoint The endpoint's name.
     * @param txName   The call's name.
     * @param supplier The actual code to invoke.
     * @param <T>      The return type.
     * @return The value returned by the {@code supplier}.
     */
    @SuppressWarnings({"unused", "try", "PMD.AvoidCatchingThrowable"})
    public static <T> T kibanaTx(final String endPoint, final String txName, final Supplier<T> supplier) {
        try (KibanaLogTransaction kibanaLogTransaction = new KibanaLogTransaction(getTxType(endPoint, txName))) {
            return supplier.get();
        } catch (Throwable throwable) {
            logError(throwable);
            throw throwable;
        }
    }

    /**
     * Wrap the call with a kibana transaction.
     *
     * @param endPoint The endpoint's name.
     * @param txName   The call's name.
     * @param wrapped The actual code to invoke.
     */
    @SuppressWarnings({"unused", "try", "PMD.AvoidCatchingThrowable"})
    public static void kibanaTx(final String endPoint, final String txName, final WrappedInvocation wrapped) {
        try (KibanaLogTransaction kibanaLogTransaction = new KibanaLogTransaction(getTxType(endPoint, txName))) {
            wrapped.invoke();
        } catch (Throwable throwable) {
            logError(throwable);
            throw throwable;
        }
    }

    private static String getTxType(final String endPoint, final String txName) {
        return format("%s.%s", endPoint, txName);
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private static void logError(final Throwable throwable) {
        LOGGER.error("Got exception '{}' with message '{}'.", throwable.getClass().getSimpleName(), throwable.getMessage(), throwable);
    }

}
