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

package org.hawaiiframework.sample.web.resource;

import org.hawaiiframework.sample.model.Recipe;
import org.hawaiiframework.web.resource.AbstractResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.stream.Collectors;

/**
 * @author Marcel Overdijk
 */
@Component
public class RecipeResourceAssembler extends AbstractResourceAssembler<Recipe, RecipeResource> {

    private final IngredientResourceAssembler ingredientResourceAssembler;

    @Autowired
    public RecipeResourceAssembler(final IngredientResourceAssembler ingredientResourceAssembler) {
        Assert.notNull(ingredientResourceAssembler, "IngredientResourceAssembler must not be null");
        this.ingredientResourceAssembler = ingredientResourceAssembler;
    }

    @Override
    public RecipeResource toResource(Recipe recipe) {
        RecipeResource resource = new RecipeResource();
        resource.setId(recipe.getId());
        resource.setCreatedDate(recipe.getCreatedDate());
        resource.setName(recipe.getName());
        resource.setDescription(recipe.getDescription());
        resource.setPreparationTime(recipe.getPreparationTime());
        resource.setCookTime(recipe.getCookTime());
        resource.setReadyTime(recipe.getReadyTime());
        resource.setIngredients(ingredientResourceAssembler.toResources(recipe.getIngredients()));
        resource.setInstructions(recipe.getInstructions().stream().map(i -> i.getDescription()).collect(Collectors.toList()));
        return resource;
    }
}
