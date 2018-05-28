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
package org.hawaiiframework.logging.logback;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import org.apache.commons.lang3.StringUtils;

import static org.hawaiiframework.logging.logback.CharacterConstants.INDENT;
import static org.hawaiiframework.logging.logback.CharacterConstants.NEW_LINE;

/**
 * Converter from IThrowableProxy to logfile format(s).
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class IThrowableProxyConverter {

    /**
     * Converts an IThrowableProxy to a formatted String value.
     *
     * @param throwableProxy the non null value for throwable.
     * @return a formatted String with the exception message, the classname and the stacktrace.
     */
    public String convert(final IThrowableProxy throwableProxy) {
        final StringBuilder stringBuilder = new StringBuilder();
        append(stringBuilder, throwableProxy);

        appendCause(stringBuilder, throwableProxy);
        return stringBuilder.toString();
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private void appendCause(final StringBuilder stringBuilder, final IThrowableProxy throwableProxy) {
        IThrowableProxy cause = throwableProxy.getCause();
        while (cause != null) {
            stringBuilder.append(NEW_LINE).append("Caused by: ");
            append(stringBuilder, cause);
            cause = cause.getCause();
        }
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private void append(final StringBuilder stringBuilder, final IThrowableProxy throwable) {
        stringBuilder.append(throwable.getClassName());
        final String message = throwable.getMessage();
        if (StringUtils.isNotEmpty(message)) {
            stringBuilder.append(": ").append(message);
        }

        final StackTraceElementProxy[] stackTraceElements = throwable.getStackTraceElementProxyArray();
        final int numberOfCommonFrames = throwable.getCommonFrames();
        for (int i = 0; i < stackTraceElements.length - numberOfCommonFrames; i++) {
            stringBuilder.append(NEW_LINE).append(INDENT).append(getSTEAsString(stackTraceElements[i]));
        }
        if (numberOfCommonFrames > 0) {
            stringBuilder.append(NEW_LINE).append(INDENT).append("... ").append(numberOfCommonFrames).append(" more");
        }
    }

    private String getSTEAsString(final StackTraceElementProxy stackTraceElement) {
        return stackTraceElement.getSTEAsString();
    }
}
