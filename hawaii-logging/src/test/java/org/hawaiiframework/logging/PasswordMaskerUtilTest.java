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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.hawaiiframework.logging.util.PasswordMaskerUtil;
import org.junit.Before;
import org.junit.Test;

public class PasswordMaskerUtilTest {

  private static final String PASSWORD = "%5Ji!@00Aade'_),.:{}[] '(*\"&^%$#@!";
  private static final String MASKED_PASSWORD = "***";

  private PasswordMaskerUtil passwordMaskerUtil;

  @Before
  public void setUp() {
    List<String> fieldsToMask = new ArrayList<>();
    fieldsToMask.add("password");
    fieldsToMask.add("keyPassphrase");

    passwordMaskerUtil = new PasswordMaskerUtil(fieldsToMask);
  }

  @Test
  public void test() throws Exception {
    URL resource = getClass().getClassLoader().getResource("password-masking/");
    Path directory = Paths.get(resource.toURI());
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
      for (Path path : directoryStream) {
        System.out.println("Got file '" + path.toString() + "'.");

        String content = new String(Files.readAllBytes(path), UTF_8);
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

  private static String getInput(String content) throws UnsupportedEncodingException {
    String input =
        StringUtils.replace(content, "{{PASSWORD}}", StringEscapeUtils.escapeJson(PASSWORD));
    input =
        StringUtils.replace(
            input, "{{URI_ESCAPED_PASSWORD}}", URLEncoder.encode(PASSWORD, "UTF-8"));
    input =
        StringUtils.replace(
            input, "{{XML_ESCAPED_PASSWORD}}", StringEscapeUtils.escapeXml11(PASSWORD));
    return input;
  }

  private static String getExpectedOutput(String content) {
    String expectedOutput = StringUtils.replace(content, "{{PASSWORD}}", MASKED_PASSWORD);
    expectedOutput =
        StringUtils.replace(expectedOutput, "{{URI_ESCAPED_PASSWORD}}", MASKED_PASSWORD);
    expectedOutput =
        StringUtils.replace(expectedOutput, "{{XML_ESCAPED_PASSWORD}}", MASKED_PASSWORD);
    return expectedOutput;
  }
}
