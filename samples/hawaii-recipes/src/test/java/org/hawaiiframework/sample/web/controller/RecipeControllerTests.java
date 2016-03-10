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

package org.hawaiiframework.sample.web.controller;

import org.hawaiiframework.sample.Application;
import org.hawaiiframework.test.mockmvc.AbstractMockMvcTest;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;

import static org.hamcrest.Matchers.hasSize;
import static org.hawaiiframework.sample.web.Paths.RECIPES_LIST_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Marcel Overdijk
 */
@SpringApplicationConfiguration(classes = Application.class)
public class RecipeControllerTests extends AbstractMockMvcTest {

    @Test
    public void list() throws Exception {
        mockMvc.perform(get(RECIPES_LIST_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
