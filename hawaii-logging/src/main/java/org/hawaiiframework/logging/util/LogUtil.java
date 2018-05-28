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
package org.hawaiiframework.logging.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * LogUtil to indent data.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public final class LogUtil {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class);

    /**
     * The configured newline to look for.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    private LogUtil() {
        // Utility constructor.
    }

    /**
     * Indent the {@code value} with the given {@code indent}.
     */
    public static String indent(final String value, final String indent) {
        return indent + value.replace(NEW_LINE, String.format("%n%s", indent));
    }

    /**
     * Writes the {@code input} to the file {@code filename} in the directory {@code parentDir}.
     */
    public static void writeToFile(final Path parentDir, final String filename, final InputStream input) throws IOException {
        if (!Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                LOGGER.error("Error creating directory '{}'", parentDir.toAbsolutePath(), e);
            }
        }
        if (Files.exists(parentDir)) {
            final Path outputFile = parentDir.resolve(filename);
            writeInputToFile(input, outputFile);
        } else {
            LOGGER.error("Somehow we cannot create '{}'.", parentDir.toAbsolutePath());
        }
    }

    private static void writeInputToFile(final InputStream input, final Path outputFile) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(outputFile)) {
            IOUtils.copy(input, outputStream);
        }
        LOGGER.info("Wrote to file '{}'.", outputFile.toAbsolutePath());
    }
}
