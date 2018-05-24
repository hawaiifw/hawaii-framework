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

package org.hawaiiframework.sample.web.resource;

import org.hawaiiframework.converter.AbstractModelConverter;
import org.hawaiiframework.sample.model.Ingredient;
import org.springframework.stereotype.Component;

/**
 * @author Marcel Overdijk
 */
@Component
public class IngredientResourceAssembler extends AbstractModelConverter<Ingredient, IngredientResource> {

    public IngredientResourceAssembler() {
        super(IngredientResource.class);
    }

    @Override
    public void convert(Ingredient ingredient, IngredientResource resource) {
        resource.setQuantity(ingredient.getQuantity());
        resource.setDescription(ingredient.getDescription());
    }
}
