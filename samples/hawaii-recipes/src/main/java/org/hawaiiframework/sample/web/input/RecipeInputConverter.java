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

package org.hawaiiframework.sample.web.input;

import org.hawaiiframework.sample.model.Recipe;
import org.hawaiiframework.web.input.AbstractInputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * @author Marcel Overdijk
 */
@Component
public class RecipeInputConverter extends AbstractInputConverter<RecipeInput, Recipe> {

    private final IngredientInputConverter ingredientInputConverter;

    @Autowired
    public RecipeInputConverter(final IngredientInputConverter ingredientInputConverter) {
        super(Recipe.class);
        this.ingredientInputConverter = requireNonNull(ingredientInputConverter, "'ingredientInputConverter' must not be null");
    }

    @Override
    public void convert(final RecipeInput input, final Recipe recipe) {
        recipe.setName(input.getName());
        recipe.setEmail(input.getEmail());
        recipe.setDescription(input.getDescription());
        recipe.setPreparationTime(input.getPreparationTime());
        recipe.setCookTime(input.getCookTime());
        recipe.setReadyTime(input.getReadyTime());
        recipe.setIngredients(ingredientInputConverter.convert(input.getIngredients()));
        recipe.setInstructions(input.getInstructions());
    }
}
