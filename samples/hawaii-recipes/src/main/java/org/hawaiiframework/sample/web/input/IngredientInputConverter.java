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

import org.hawaiiframework.sample.model.Ingredient;
import org.hawaiiframework.web.input.AbstractInputConverter;
import org.springframework.stereotype.Component;

/**
 * @author Marcel Overdijk
 */
@Component
public class IngredientInputConverter extends AbstractInputConverter<IngredientInput, Ingredient> {


    public IngredientInputConverter() {
        super(Ingredient.class);
    }

    @Override
    public void convert(final IngredientInput input, final Ingredient ingredient) {
        ingredient.setDescription(input.getDescription());
        ingredient.setQuantity(input.getQuantity());
    }
}
