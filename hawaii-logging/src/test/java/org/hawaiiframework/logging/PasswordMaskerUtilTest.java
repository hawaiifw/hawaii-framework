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
package org.hawaiiframework.logging;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.logging.util.PasswordMaskerUtil;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class PasswordMaskerUtilTest {


    private static final String PASSWORD = "%5Ji!@00Aade'_),.:{}[] '(*\"&^%$#@!";
    private static final String MASKED_PASSWORD = "***";

    private PasswordMaskerUtil passwordMaskerUtil = new PasswordMaskerUtil();


    @Test
    public void test() throws Exception {
        final URL resource = getClass().getClassLoader().getResource("password-masking/");
        final Path directory = Paths.get(resource.toURI());
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path path : directoryStream) {
                System.out.println("Got file '" + path.toString() + "'.");

                String content = new String(Files.readAllBytes(path));
                String input = getInput(content);

                String expectedOutput = getExpectedOutput(content);

                System.out.println(input);
                String output = passwordMaskerUtil.maskPasswordsIn(input);
                System.out.println(output);
                System.out.println();
                assertThat(output, is(equalTo(expectedOutput)));
            }
        }

    }

    private String getInput(final String content) throws UnsupportedEncodingException {
        String input = StringUtils.replace(content, "{{PASSWORD}}", StringEscapeUtils.escapeJson(PASSWORD));
        input = StringUtils.replace(input, "{{URI_ESCAPED_PASSWORD}}", URLEncoder.encode(PASSWORD, "UTF-8"));
        input = StringUtils.replace(input, "{{XML_ESCAPED_PASSWORD}}",  StringEscapeUtils.escapeXml11(PASSWORD));
        return input;
    }

    private String getExpectedOutput(final String content) {
        String expectedOutput = StringUtils.replace(content, "{{PASSWORD}}", MASKED_PASSWORD);
        expectedOutput = StringUtils.replace(expectedOutput, "{{URI_ESCAPED_PASSWORD}}", MASKED_PASSWORD);
        expectedOutput = StringUtils.replace(expectedOutput, "{{XML_ESCAPED_PASSWORD}}", MASKED_PASSWORD);
        return expectedOutput;
    }
}
