/*
 * Copyright 2002-2012 the original author or authors.
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

package org.hawaiiframework.async.runable;

import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.TransactionId;
import org.hawaiiframework.logging.model.kibana.enums.HawaiiKibanaLogFieldNames;
import org.springframework.scheduling.support.MethodInvokingRunnable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.UUID;

/**
 * This copy of the Spring original version adds a TX id for logging purposes.
 *
 * Variant of {@link MethodInvokingRunnable} meant to be used for processing
 * of no-arg scheduled methods. Propagates user exceptions to the caller,
 * assuming that an error strategy for Runnables is in place.
 *
 * @author Juergen Hoeller
 * @author Rutger Lubbers
 * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
 * @since 3.0.6
 */
public class HawaiiScheduledMethodRunnable implements Runnable {

    /**
     * The object to invoke the method upon.
     */
    private final Object target;

    /**
     * The method to invoke.
     */
    private final Method method;

    /**
     * Constructor.
     */
    public HawaiiScheduledMethodRunnable(final Object target, final Method method) {
        this.target = target;
        this.method = method;
    }

    /**
     * Constructor.
     */
    public HawaiiScheduledMethodRunnable(final Object target, final String methodName) throws NoSuchMethodException {
        this.target = target;
        this.method = target.getClass().getMethod(methodName);
    }


    @SuppressWarnings("PMD.CommentRequired")
    public Object getTarget() {
        return this.target;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public Method getMethod() {
        return this.method;
    }


    /**
     * Runs the scheduled method.
     *
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            TransactionId.set(UUID.randomUUID());
            KibanaLogFields.set(HawaiiKibanaLogFieldNames.TX_ID, TransactionId.get());
            ReflectionUtils.makeAccessible(method);
            method.invoke(target);
        } catch (final InvocationTargetException ex) {
            ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
        } catch (final IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
        } finally {
            KibanaLogFields.clear();
        }
    }
}
