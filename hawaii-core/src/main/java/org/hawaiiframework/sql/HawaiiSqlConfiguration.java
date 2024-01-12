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

package org.hawaiiframework.sql;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResourceLoader;

/**
 * @author Rutger Lubbers
 * @since 6.0.0
 */
@Configuration
public class HawaiiSqlConfiguration {

  @Bean
  @ConditionalOnMissingBean(SqlQueryResolver.class)
  public SqlQueryResolver resourceSqlQueryResolver() {
    SqlQueryResolverComposite sqlQueryResolver = new SqlQueryResolverComposite();

    FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();

    ResourceSqlQueryResolver fileSystemQueryResolver =
        new ResourceSqlQueryResolver(fileSystemResourceLoader);
    fileSystemQueryResolver.setPrefix("src/main/resources/");

    ResourceSqlQueryResolver resourceSqlQueryResolver = new ResourceSqlQueryResolver();

    sqlQueryResolver.setSqlQueryResolvers(
        List.of(fileSystemQueryResolver, resourceSqlQueryResolver));
    return sqlQueryResolver;
  }
}
