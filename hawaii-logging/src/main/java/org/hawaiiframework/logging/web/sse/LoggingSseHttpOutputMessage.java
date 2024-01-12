/*
 * Copyright 2015-2022 the original author or authors.
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
package org.hawaiiframework.logging.web.sse;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

public class LoggingSseHttpOutputMessage implements HttpOutputMessage {

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

  @Override
  public OutputStream getBody() throws IOException {
    return outputStream;
  }

  @Override
  public HttpHeaders getHeaders() {
    return new HttpHeaders();
  }

  public String getContents() {
    return outputStream.toString(UTF_8);
  }
}
