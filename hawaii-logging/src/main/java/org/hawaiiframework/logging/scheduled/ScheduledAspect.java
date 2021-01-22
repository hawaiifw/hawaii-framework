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
package org.hawaiiframework.logging.scheduled;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hawaiiframework.logging.model.KibanaLogFields;
import org.hawaiiframework.logging.model.RequestId;
import org.hawaiiframework.logging.model.TransactionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.REQUEST_ID;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_ID;

/**
 * Aspect around @{@link org.springframework.scheduling.annotation.Scheduled} annotation to allow participating in Kibana tx's.
 */
@Aspect
public class ScheduledAspect {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledAspect.class);

    /**
     * Create an around advise for the {@link org.springframework.scheduling.annotation.Scheduled} annotation.
     *
     * @param pjp The proceeding join point.
     * @return The original return value.
     * @throws Throwable in case of an error.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Around("execution (@org.springframework.scheduling.annotation.Scheduled  * *.*(..))")
    public Object traceBackgroundThread(final ProceedingJoinPoint pjp) throws Throwable {
        try {
            final UUID uuid = UUID.randomUUID();

            RequestId.set(uuid);
            KibanaLogFields.tag(REQUEST_ID, RequestId.get());

            TransactionId.set(uuid);
            KibanaLogFields.tag(TX_ID, TransactionId.get());

            LOGGER.trace("Started scheduled task with tx id '{}'.", TransactionId.get());
            return pjp.proceed();
        } catch (Exception ex) {
            LOGGER.error("Caught error '{}'.", ex, ex);
            throw ex;
        } finally {
            RequestId.remove();
            TransactionId.remove();
            KibanaLogFields.clear();
        }
    }
}
