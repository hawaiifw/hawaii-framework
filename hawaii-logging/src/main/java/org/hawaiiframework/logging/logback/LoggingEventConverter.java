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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.hawaiiframework.logging.model.KibanaLogFields;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.hawaiiframework.logging.logback.CharacterConstants.INDENT;
import static org.hawaiiframework.logging.logback.CharacterConstants.NEW_LINE;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.LOG_LOCATION;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.LOG_TYPE;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.THREAD;
import static org.hawaiiframework.logging.util.IndentUtil.indent;

/**
 * Converts logging events to a readable log format.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class LoggingEventConverter {

    /**
     * The log line format.
     */
    protected static final String LOG_LINE_FORMAT = "%s %s %s %s message=#%s%n";

    /**
     * Date pattern for logging output.
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";

    /**
     * our throwable proxy converter.
     */
    private final IThrowableProxyConverter iThrowableProxyConverter;

    /**
     * Constructor for normal usage.
     */
    public LoggingEventConverter() {
        this.iThrowableProxyConverter = new IThrowableProxyConverter();
    }

    /**
     * Constructor for unit testing.
     *
     * @param iThrowableProxyConverter the converter to be used (e.g. a mock).
     */
    public LoggingEventConverter(final IThrowableProxyConverter iThrowableProxyConverter) {
        this.iThrowableProxyConverter = iThrowableProxyConverter;
    }

    /**
     * Converts the LoggingEvent to a loggable string.
     *
     * @param event the non-null event.
     * @return a nicely formatted String.
     */
    public String convert(final LoggingEvent event) {
        KibanaLogFields.tag(LOG_LOCATION, getLogLocation(getFirstElement(event)));
        KibanaLogFields.tag(THREAD, event.getThreadName());

        final StringBuilder message = new StringBuilder(event.getFormattedMessage());
        if (event.getThrowableProxy() != null) {
            final IThrowableProxy throwable = event.getThrowableProxy();
            message.append(NEW_LINE).append(iThrowableProxyConverter.convert(throwable));
        }

        return createLogLine(getTimestamp(event), getLogLevel(event.getLevel()), indent(message.toString(), INDENT));
    }

    private String getTimestamp(final LoggingEvent event) {
        final Date timestamp = new Date(event.getTimeStamp());
        return getTimestamp(timestamp);
    }

    /**
     * Public method to check whether the timestamp is correct for the last week of december.
     *
     * @param timestamp The timestamp to convert.
     * @return The converted timestamp.
     */
    public String getTimestamp(final Date timestamp) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        return simpleDateFormat.format(timestamp);
    }


    private String getLogLocation(final StackTraceElement stackTraceElement) {
        return stackTraceElement.getClassName() + ":" + stackTraceElement.getLineNumber();
    }

    private StackTraceElement getFirstElement(final LoggingEvent event) {
        return getFirstElement(event.getCallerData());
    }

    private StackTraceElement getFirstElement(final StackTraceElement... stackTrace) {
        return stackTrace[0];
    }

    private String getLogLevel(final Level logLevel) {
        return logLevel.levelStr;
    }

    private String createLogLine(final String timestamp, final String level, final String message) {
        final List<String> values = new ArrayList<>();
        values.add(timestamp);
        values.add(level);
        values.add(KibanaLogFields.getOrDefault(LOG_TYPE, "-"));
        values.add(KibanaLogFields.getValuesAsLogString());
        values.add(message);

        return String.format(LOG_LINE_FORMAT, values.toArray());
    }

}
