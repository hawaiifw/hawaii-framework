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

package org.hawaiiframework.sample.web.controller;

import org.hawaiiframework.sample.model.Recipe;
import org.hawaiiframework.sample.repository.RecipeRepository;
import org.hawaiiframework.sample.web.input.RecipeInput;
import org.hawaiiframework.sample.web.input.RecipeInputConverter;
import org.hawaiiframework.sample.web.input.RecipeInputValidator;
import org.hawaiiframework.sample.web.resource.RecipeResource;
import org.hawaiiframework.sample.web.resource.RecipeResourceAssembler;
import org.hawaiiframework.time.HawaiiTime;
import org.hawaiiframework.web.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.hawaiiframework.sample.web.Paths.RECIPES_CREATE_PATH;
import static org.hawaiiframework.sample.web.Paths.RECIPES_DELETE_PATH;
import static org.hawaiiframework.sample.web.Paths.RECIPES_GET_PATH;
import static org.hawaiiframework.sample.web.Paths.RECIPES_LIST_PATH;
import static org.hawaiiframework.sample.web.Paths.RECIPES_UPDATE_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Marcel Overdijk
 */
@RestController
public class RecipeController {

    private static Logger logger = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeRepository recipeRepository;
    private final RecipeResourceAssembler recipeResourceAssembler;
    private final RecipeInputConverter recipeInputConverter;
    private final RecipeInputValidator recipeInputValidator;
    private final HawaiiTime hawaiiTime;

    @Autowired
    public RecipeController(final RecipeRepository recipeRepository, final RecipeResourceAssembler recipeResourceAssembler,
            final RecipeInputConverter recipeInputConverter, final RecipeInputValidator recipeInputValidator, final HawaiiTime hawaiiTime) {
        this.recipeRepository = requireNonNull(recipeRepository, "'recipeRepository' must not be null");
        this.recipeResourceAssembler = requireNonNull(recipeResourceAssembler, "'recipeResourceAssembler' must not be null");
        this.recipeInputConverter = requireNonNull(recipeInputConverter, "'recipeInputConverter' must not be null");
        this.recipeInputValidator = requireNonNull(recipeInputValidator, "'recipeInputValidator' must not be null");
        this.hawaiiTime = requireNonNull(hawaiiTime, "'hawaiiTime' must not be null");
    }

    @GetMapping(path = RECIPES_LIST_PATH, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {

        logger.info("list called");

        // Retrieve all recipes from repository
        List<Recipe> recipes = recipeRepository.findAll();

        // Convert retrieved recipes to recipe resources to be returned to client
        List<RecipeResource> recipeResources = recipeResourceAssembler.convert(recipes);

        // Return recipe resources to client
        return ResponseEntity.ok().body(recipeResources);
    }

    @PostMapping(path = RECIPES_CREATE_PATH, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody RecipeInput recipeInput) {

        logger.info("create called with input: {}", recipeInput);

        // Validate recipe input; and throw validation exception in case of validation errors
        recipeInputValidator.validateAndThrow(recipeInput);

        // Convert recipe input to recipe
        Recipe recipe = recipeInputConverter.convert(recipeInput);
        recipe.setCreatedDate(hawaiiTime.localDate());

        // Save recipe to repository
        recipeRepository.save(recipe);

        // Convert saved recipe to recipe resource to be returned to client
        RecipeResource recipeResource = recipeResourceAssembler.convert(recipe);

        // Return recipe resource to client
        return ResponseEntity.ok().body(recipeResource);
    }

    @GetMapping(path = RECIPES_GET_PATH, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get(@PathVariable("id") Long id) {

        logger.info("get called with id: ", id);

        // Retrieve recipe from repository
        Recipe recipe = recipeRepository.findOne(id);

        // If recipe not found throw resource not found exception
        if (recipe == null) {
            throw new ResourceNotFoundException(String.format("Recipe %d not found", id));
        }

        // Convert recipe to recipe resource to be returned to client
        RecipeResource recipeRsource = recipeResourceAssembler.convert(recipe);

        // Return recipe resource to client
        return ResponseEntity.ok().body(recipeRsource);
    }

    @PutMapping(path = RECIPES_UPDATE_PATH, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable("id") Long id,
            @RequestBody RecipeInput recipeInput) {

        logger.info("update called with id: {}, input: {}", id, recipeInput);

        // Retrieve recipe from repository
        Recipe recipe = recipeRepository.findOne(id);

        // If recipe not found throw resource not found exception
        if (recipe == null) {
            throw new ResourceNotFoundException(String.format("Recipe %d not found", id));
        }

        // Validate recipe input; and throw validation exception in case of validation errors
        recipeInputValidator.validateAndThrow(recipeInput);

        // TODO verify if it is allowed to update recipe

        // Convert recipe input to recipe
        recipeInputConverter.convert(recipeInput, recipe);

        // Save recipe to repository
        recipeRepository.save(recipe);

        // Convert saved recipe to recipe resource to be returned to client
        RecipeResource recipeResource = recipeResourceAssembler.convert(recipe);

        // Return recipe resource to client
        return ResponseEntity.ok().body(recipeResource);
    }

    @DeleteMapping(path = RECIPES_DELETE_PATH)
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {

        logger.info("delete called with id: {}", id);

        // Retrieve recipe from repository
        Recipe recipe = recipeRepository.findOne(id);

        // If recipe not found throw resource not found exception
        if (recipe == null) {
            throw new ResourceNotFoundException(String.format("Recipe %d not found", id));
        }

        // TODO verify if it is allowed to delete recipe

        // Delete recipe from repository
        recipeRepository.delete(id);

        // Return no content response to client
        return ResponseEntity.noContent().build();
    }
}
