
package org.hawaiiframework.sample.config;

import org.hawaiiframework.sample.Application;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

/**
 * This configuration class is needed to use JSR-310 date types.
 * <p>
 * From Spring Boot 1.4 onwards these converts are registered automatically, and this config class
 * can be removed completely.
 * <p>
 * See https://github.com/spring-projects/spring-boot/issues/2721
 */
@Configuration
@EntityScan(basePackageClasses = {Application.class, Jsr310JpaConverters.class})
public class DataConfig {
}
