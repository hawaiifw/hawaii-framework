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

package org.hawaiiframework.async.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.lang.Nullable;

/**
 * Utility to add beans to Spring's bean definition registry.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 3.0.0
 */
public class BeanRegistrar {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanRegistrar.class);

    /**
     * Spring's bean definition registry.
     */
    private final BeanDefinitionRegistry registry;

    /**
     * The constructor.
     *
     * @param registry Spring's bean definition registry.
     */
    public BeanRegistrar(final BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * Create a {@link GenericBeanDefinition} of the specified class and register it with the registry.
     *
     * @param beanName the bean name
     * @param clazz    the bean class
     */
    public void registerBean(
        final String beanName,
        final Class<?> clazz) {
        registerBean(beanName, clazz, null);
    }

    /**
     * Create a {@link GenericBeanDefinition} of the specified class and register it with the registry.
     *
     * @param beanName                  the bean name
     * @param clazz                     the bean class
     * @param constructorArgumentValues the constructor arguments.
     */
    public void registerBean(
        final String beanName,
        final Class<?> clazz,
        @Nullable final ConstructorArgumentValues constructorArgumentValues) {
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setAutowireMode(ConfigurableListableBeanFactory.AUTOWIRE_NO);
        beanDefinition.setDependencyCheck(GenericBeanDefinition.DEPENDENCY_CHECK_NONE);
        if (constructorArgumentValues != null) {
            beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
        }

        LOGGER.trace("Registring bean '{}' of type '{}'.", beanName, clazz.getSimpleName());

        registry.registerBeanDefinition(beanName, beanDefinition);
    }

}
