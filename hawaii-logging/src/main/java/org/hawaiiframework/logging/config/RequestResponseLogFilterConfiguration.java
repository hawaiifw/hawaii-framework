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
package org.hawaiiframework.logging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class to hold the configuration properties for the logging filter.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
@ConfigurationProperties(prefix = "hawaii.requestLogging")
public class RequestResponseLogFilterConfiguration extends LoggingFilterProperties {

    /**
     * Write large output and non matched output to a file?
     */
    private boolean fallbackToFile;

    /**
     * The directory to log the output to if it is too big (or is not of the type we can log).
     */
    private String directory = System.getProperty("java.io.tmpdir");

    /**
     * The max log size for the console / log file(s).
     */
    private String maxLogSize = "50k";

    /**
     * The content types we log to the console / log file(s).
     */
    private List<String> allowedContentTypes = new ArrayList<>();

    @SuppressWarnings("PMD.CommentRequired")
    public boolean isFallbackToFile() {
        return fallbackToFile;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setFallbackToFile(final boolean fallbackToFile) {
        this.fallbackToFile = fallbackToFile;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getDirectory() {
        return directory;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setDirectory(final String directory) {
        this.directory = directory;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public String getMaxLogSize() {
        return maxLogSize;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setMaxLogSize(final String maxLogSize) {
        this.maxLogSize = maxLogSize;
    }

    /**
     * Get the log size in bytes.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public Long getMaxLogSizeInBytes() {
        String logSizeString = getMaxLogSize();
        logSizeString = logSizeString.toUpperCase(Locale.ENGLISH);

        long multiplication = 1L;
        if (logSizeString.endsWith("K")) {
            multiplication = 1000L;
            logSizeString = logSizeString.substring(0, logSizeString.length() - 1);
        } else if (logSizeString.endsWith("M")) {
            multiplication = 1000L * 1000;
            logSizeString = logSizeString.substring(0, logSizeString.length() - 1);
        } else if (logSizeString.endsWith("G")) {
            multiplication = 1000L * 1000 * 1000;
            logSizeString = logSizeString.substring(0, logSizeString.length() - 1);
        }

        logSizeString = logSizeString.trim();

        return Long.parseLong(logSizeString) * multiplication;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public List<String> getAllowedContentTypes() {
        return allowedContentTypes;
    }

    @SuppressWarnings("PMD.CommentRequired")
    public void setAllowedContentTypes(final List<String> allowedContentTypes) {
        this.allowedContentTypes = allowedContentTypes;
    }
}
