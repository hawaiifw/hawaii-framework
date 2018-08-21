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

package org.hawaiiframework.sample.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * @author Marcel Overdijk
 */
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private Float preparationTime;

    @Column(nullable = false)
    private Float cookTime;

    @Column(nullable = false)
    private Float readyTime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<Ingredient> ingredients = new ArrayList<>();

    @Column(nullable = false, length = 5000)
    private String instructions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }
}
