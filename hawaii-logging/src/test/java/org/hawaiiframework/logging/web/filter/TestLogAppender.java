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

package org.hawaiiframework.logging.web.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class TestLogAppender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> loggingEvents = new ArrayList<>();

    @Override
    protected void append(final ILoggingEvent eventObject) {
        loggingEvents.add(eventObject);
    }


    public ILoggingEvent getLastLoggedEvent() {
        if (loggingEvents.isEmpty()) {
            return null;
        }

        return loggingEvents.get(loggingEvents.size() - 1);
    }

    public ILoggingEvent findEventForMessage(final String message) {
        return loggingEvents.stream()
                .filter(event -> event.getMessage().equals(message))
                .findFirst()
                .orElse(null);
    }
}
