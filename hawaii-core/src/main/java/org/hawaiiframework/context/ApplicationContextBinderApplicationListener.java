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

package org.hawaiiframework.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * {@link ApplicationListener} that binds the {@link ApplicationContext} to the Hawaii {@link ApplicationContextHolder}.
 *
 * @author Marcel Overdijk
 * @since 2.0.0
 */
public class ApplicationContextBinderApplicationListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            if (ApplicationContextHolder.getApplicationContext() != null) {
                ApplicationContextHolder.release();
            }
            final ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
            ApplicationContextHolder.bind(applicationContext);
        } else if (event instanceof ContextClosedEvent) {
            ApplicationContextHolder.release();
        }
    }
}
