/*
 * Copyright 2015-2016 the original author or authors.
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

package org.hawaiiframework.sample.web;

import org.hawaiiframework.sample.Application;
import org.hawaiiframework.test.mockmvc.AbstractMockMvcTest;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;

import java.time.OffsetDateTime;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Marcel Overdijk
 */
@SpringApplicationConfiguration(classes = Application.class)
public class HelloControllerTests extends AbstractMockMvcTest {

    private OffsetDateTime now = OffsetDateTime.now();

    @Before
    public void setUp() {
        hawaiiTime.useFixedClock(now);
    }

    @After
    public void tearDown() {
        hawaiiTime.useSystemClock();
    }

    @Test
    public void greetWithoutNameShouldReturnAlohaStranger() throws Exception {
        mockMvc.perform(get("/rest/hello/greet"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp", is(now.format(ISO_OFFSET_DATE_TIME))))
                .andExpect(jsonPath("$.greeting", is("Aloha stranger, pehea 'oe?")));
    }

    @Test
    public void greetWithNameShouldReturnAlohaWithName() throws Exception {
        mockMvc.perform(get("/rest/hello/greet")
                .param("name", "Marcel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp", is(now.format(ISO_OFFSET_DATE_TIME))))
                .andExpect(jsonPath("$.greeting", is("Aloha Marcel, pehea 'oe?")));
    }
}
