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

import org.hawaiiframework.validation.Validator;
import org.hawaiiframework.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Marcel Overdijk
 */
@Component
public class RecipeInputValidator implements Validator<RecipeInput> {

    private final IngredientInputValidator ingredientInputValidator;

    @Autowired
    public RecipeInputValidator(final IngredientInputValidator ingredientInputValidator) {
        Assert.notNull(ingredientInputValidator, "IngredientInputValidator must not be null");
        this.ingredientInputValidator = ingredientInputValidator;
    }

    @Override
    public void validate(RecipeInput recipe, ValidationResult validationResult) {
        // TODO
    }
}
