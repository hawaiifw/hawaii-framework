/*
 * Copyright 2015-2017 the original author or authors.
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
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hawaiiframework.sample.web.Paths.RECIPES_GET_PATH;
import static org.hawaiiframework.sample.web.Paths.RECIPES_LIST_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Marcel Overdijk
 */
@SpringBootTest(classes = Application.class)
public class RecipeControllerTests extends AbstractMockMvcTest {

    @Test
    public void listShouldReturnRecipes() throws Exception {
        mockMvc.perform(get(RECIPES_LIST_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].created_date", is("2016-02-15")))
                .andExpect(jsonPath("$[0].name", is("Marcel")))
                .andExpect(jsonPath("$[0].description", is("Aloha Chicken")))
                .andExpect(jsonPath("$[0].preparation_time", is(20.0)))
                .andExpect(jsonPath("$[0].cook_time", is(60.0)))
                .andExpect(jsonPath("$[0].ready_time", is(80.0)))
                .andExpect(jsonPath("$[0].ingredients", hasSize(10)))
                .andExpect(jsonPath("$[0].ingredients[0].quantity", is(4.0)))
                .andExpect(jsonPath("$[0].ingredients[0].description", is("pounds chicken thighs")))
                .andExpect(jsonPath("$[0].ingredients[1].quantity", is(1.0)))
                .andExpect(jsonPath("$[0].ingredients[1].description", is("teaspoon ground ginger")))
                .andExpect(jsonPath("$[0].ingredients[2].quantity", is(1.0)))
                .andExpect(jsonPath("$[0].ingredients[2].description", is("teaspoon paprika")))
                .andExpect(jsonPath("$[0].ingredients[3].quantity", is(1.0)))
                .andExpect(jsonPath("$[0].ingredients[3].description", is("tablespoon onion powder")))
                .andExpect(jsonPath("$[0].ingredients[4].quantity", is(2.0)))
                .andExpect(jsonPath("$[0].ingredients[4].description", is("tablespoons garlic salt")))
                .andExpect(jsonPath("$[0].ingredients[5].quantity", is(3.0)))
                .andExpect(jsonPath("$[0].ingredients[5].description", is("tablespoons cider vinegar")))
                .andExpect(jsonPath("$[0].ingredients[6].quantity", is(1.0)))
                .andExpect(jsonPath("$[0].ingredients[6].description", is("cup ketchup")))
                .andExpect(jsonPath("$[0].ingredients[7].quantity", is(0.25)))
                .andExpect(jsonPath("$[0].ingredients[7].description", is("cup soy sauce")))
                .andExpect(jsonPath("$[0].ingredients[8].quantity", is(1.0)))
                .andExpect(jsonPath("$[0].ingredients[8].description", is("(20 ounce) can crushed pineapple with juice")))
                .andExpect(jsonPath("$[0].ingredients[9].quantity", is(0.25)))
                .andExpect(jsonPath("$[0].ingredients[9].description", is("cup packed brown sugar")))
                .andExpect(jsonPath("$[0].instructions",
                        is("<p>Preheat oven to 400 degrees F (200 degrees C).</p><p>Arrange chicken pieces in a single layer in a well greased 9x13 inch baking dish. In a small bowl mix together the ginger, paprika, onion powder and garlic salt. Add the vinegar and mix well. Divide this mixture. Brush 1/2 over the chicken pieces and bake in the preheated oven for 15 minutes.</p><p>Turn the chicken pieces, baste with the remaining 1/2 of the vinegar mixture and bake for 15 minutes longer. Meanwhile, in a medium bowl combine the ketchup, soy sauce, pineapple and brown sugar. When chicken baking time is up, spoon the pineapple/soy mixture over the chicken. Bake for another 30 minutes. Serve while still hot.</p>")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].created_date", is("2016-02-16")))
                .andExpect(jsonPath("$[1].name", is("Marcel")))
                .andExpect(jsonPath("$[1].description", is("Hawaiian Banana Nut Bread")))
                .andExpect(jsonPath("$[1].preparation_time", is(10.0)))
                .andExpect(jsonPath("$[1].cook_time", is(60.0)))
                .andExpect(jsonPath("$[1].ready_time", is(80.0)))
                .andExpect(jsonPath("$[1].ingredients", hasSize(13)))
                .andExpect(jsonPath("$[1].ingredients[0].quantity", is(3.0)))
                .andExpect(jsonPath("$[1].ingredients[0].description", is("cups all-purpose flour")))
                .andExpect(jsonPath("$[1].ingredients[1].quantity", is(0.75)))
                .andExpect(jsonPath("$[1].ingredients[1].description", is("teaspoon salt")))
                .andExpect(jsonPath("$[1].ingredients[2].quantity", is(1.0)))
                .andExpect(jsonPath("$[1].ingredients[2].description", is("teaspoon baking soda")))
                .andExpect(jsonPath("$[1].ingredients[3].quantity", is(2.0)))
                .andExpect(jsonPath("$[1].ingredients[3].description", is("cups white sugar")))
                .andExpect(jsonPath("$[1].ingredients[4].quantity", is(1.0)))
                .andExpect(jsonPath("$[1].ingredients[4].description", is("teaspoon ground cinnamon")))
                .andExpect(jsonPath("$[1].ingredients[5].quantity", is(1.0)))
                .andExpect(jsonPath("$[1].ingredients[5].description", is("cup chopped walnuts")))
                .andExpect(jsonPath("$[1].ingredients[6].quantity", is(3.0)))
                .andExpect(jsonPath("$[1].ingredients[6].description", is("eggs, beaten")))
                .andExpect(jsonPath("$[1].ingredients[7].quantity", is(1.0)))
                .andExpect(jsonPath("$[1].ingredients[7].description", is("cup vegetable oil")))
                .andExpect(jsonPath("$[1].ingredients[8].quantity", is(2.0)))
                .andExpect(jsonPath("$[1].ingredients[8].description", is("cups mashed very ripe banana")))
                .andExpect(jsonPath("$[1].ingredients[9].quantity", is(1.0)))
                .andExpect(jsonPath("$[1].ingredients[9].description", is("(8 ounce) can crushed pineapple, drained")))
                .andExpect(jsonPath("$[1].ingredients[10].quantity", is(2.0)))
                .andExpect(jsonPath("$[1].ingredients[10].description", is("teaspoons vanilla extract")))
                .andExpect(jsonPath("$[1].ingredients[11].quantity", is(1.0)))
                .andExpect(jsonPath("$[1].ingredients[11].description", is("cup flaked coconut")))
                .andExpect(jsonPath("$[1].ingredients[12].quantity", is(1.0)))
                .andExpect(jsonPath("$[1].ingredients[12].description", is("cup maraschino cherries, diced")))
                .andExpect(jsonPath("$[1].instructions",
                        is("<p>Preheat oven to 350 degrees F (175 degrees C). Grease two 9x5 inch loaf pans.</p><p>In a large mixing bowl, combine the flour, salt, baking soda, sugar and cinnamon. Add the walnuts, eggs, old, banana, pineapple, vanilla, cocounu and cherries; stir just until blended. Pour batter evenly into the prepared pans.</p><p>Bake at 250 degrees F (175 degrees C) for 60 minutes, or until a tooth pick inserted into the center of a loaf comes out clean. Cool in the pan for 10 minutes, then remove to a wire rack to cool completely.</p>")));
    }

    @Test
    public void getShouldReturnRecipe() throws Exception {
        mockMvc.perform(get(RECIPES_GET_PATH, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.created_date", is("2016-02-15")))
                .andExpect(jsonPath("$.name", is("Marcel")))
                .andExpect(jsonPath("$.description", is("Aloha Chicken")))
                .andExpect(jsonPath("$.preparation_time", is(20.0)))
                .andExpect(jsonPath("$.cook_time", is(60.0)))
                .andExpect(jsonPath("$.ready_time", is(80.0)))
                .andExpect(jsonPath("$.ingredients", hasSize(10)))
                .andExpect(jsonPath("$.ingredients[0].quantity", is(4.0)))
                .andExpect(jsonPath("$.ingredients[0].description", is("pounds chicken thighs")))
                .andExpect(jsonPath("$.ingredients[1].quantity", is(1.0)))
                .andExpect(jsonPath("$.ingredients[1].description", is("teaspoon ground ginger")))
                .andExpect(jsonPath("$.ingredients[2].quantity", is(1.0)))
                .andExpect(jsonPath("$.ingredients[2].description", is("teaspoon paprika")))
                .andExpect(jsonPath("$.ingredients[3].quantity", is(1.0)))
                .andExpect(jsonPath("$.ingredients[3].description", is("tablespoon onion powder")))
                .andExpect(jsonPath("$.ingredients[4].quantity", is(2.0)))
                .andExpect(jsonPath("$.ingredients[4].description", is("tablespoons garlic salt")))
                .andExpect(jsonPath("$.ingredients[5].quantity", is(3.0)))
                .andExpect(jsonPath("$.ingredients[5].description", is("tablespoons cider vinegar")))
                .andExpect(jsonPath("$.ingredients[6].quantity", is(1.0)))
                .andExpect(jsonPath("$.ingredients[6].description", is("cup ketchup")))
                .andExpect(jsonPath("$.ingredients[7].quantity", is(0.25)))
                .andExpect(jsonPath("$.ingredients[7].description", is("cup soy sauce")))
                .andExpect(jsonPath("$.ingredients[8].quantity", is(1.0)))
                .andExpect(jsonPath("$.ingredients[8].description", is("(20 ounce) can crushed pineapple with juice")))
                .andExpect(jsonPath("$.ingredients[9].quantity", is(0.25)))
                .andExpect(jsonPath("$.ingredients[9].description", is("cup packed brown sugar")))
                .andExpect(jsonPath("$.instructions",
                        is("<p>Preheat oven to 400 degrees F (200 degrees C).</p><p>Arrange chicken pieces in a single layer in a well greased 9x13 inch baking dish. In a small bowl mix together the ginger, paprika, onion powder and garlic salt. Add the vinegar and mix well. Divide this mixture. Brush 1/2 over the chicken pieces and bake in the preheated oven for 15 minutes.</p><p>Turn the chicken pieces, baste with the remaining 1/2 of the vinegar mixture and bake for 15 minutes longer. Meanwhile, in a medium bowl combine the ketchup, soy sauce, pineapple and brown sugar. When chicken baking time is up, spoon the pineapple/soy mixture over the chicken. Bake for another 30 minutes. Serve while still hot.</p>")));
    }
}
