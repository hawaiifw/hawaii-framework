/*
 * Copyright 2015-2018 the original author or authors.
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

package org.hawaiiframework.async.timeout;

import org.hawaiiframework.async.HawaiiAsyncRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task that stops another scheduled task if the scheduled task's timeout has been reached.
 * <p>
 * We first try to remove the scheduled task from the executor, so it will not be executed.
 * If this fails we will try to abort the scheduled tasks.
 * <p>
 * For this the guarded task must register it's {@link TaskAbortStrategy} in the {@link SharedTaskContext}.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public class TimeoutGuardTask extends HawaiiAsyncRunnable {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutGuardTask.class);

    /**
     * Create a new {@link TimeoutGuardTask} instance with the given {@code taskExecutor}.
     */
    public TimeoutGuardTask(final SharedTaskContext sharedTaskContext) {
        super(sharedTaskContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRun() {
        LOGGER.trace("Executing guard task.");
        sharedTaskContext.timeout();
    }

}
