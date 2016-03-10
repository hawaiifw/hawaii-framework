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

package org.hawaiiframework.sample.web.input;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * @author Marcel Overdijk
 */
public class RecipeInput {

    private String name;
    private String description;
    private Float preparationTime;
    private Float cookTime;
    private Float readyTime;
    private List<IngredientInput> ingredients = new ArrayList<>();
    private List<String> instructions = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Float preparationTime) {
        this.preparationTime = preparationTime;
    }

    public Float getCookTime() {
        return cookTime;
    }

    public void setCookTime(Float cookTime) {
        this.cookTime = cookTime;
    }

    public Float getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(Float readyTime) {
        this.readyTime = readyTime;
    }

    public List<IngredientInput> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientInput> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }
}
