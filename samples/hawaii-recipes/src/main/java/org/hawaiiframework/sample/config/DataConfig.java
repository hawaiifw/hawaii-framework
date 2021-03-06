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
package org.hawaiiframework.sample.config;

import org.hawaiiframework.sample.Application;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

/**
 * This configuration class is needed to use JSR-310 date types.
 * <p>
 * From Spring Boot 1.4 onwards these converts are registered automatically, and this config class can be removed completely.
 * <p>
 * See https://github.com/spring-projects/spring-boot/issues/2721
 */
@Configuration
@EntityScan(basePackageClasses = {Application.class, Jsr310JpaConverters.class})
public class DataConfig {

}
