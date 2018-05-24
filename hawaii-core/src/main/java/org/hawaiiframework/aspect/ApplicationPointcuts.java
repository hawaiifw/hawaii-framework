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

package org.hawaiiframework.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author Marcel Overdijk
 * @since 2.0.0
 */
@Aspect
@SuppressWarnings("PMD.UncommentedEmptyMethodBody")
public class ApplicationPointcuts {

    @Pointcut("execution(public * *(..))")
    public void anyPublicMethod() {
    }

    @Pointcut("@target(org.springframework.stereotype.Component)")
    public void isComponent() {
    }

    @Pointcut("@target(org.springframework.stereotype.Controller) || @target(org.springframework.web.bind.annotation.RestController)")
    public void isController() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void isExceptionHandler() {
    }

    @Pointcut("@target(org.springframework.stereotype.Repository)")
    public void isRepository() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void isRequestMapping() {
    }

    @Pointcut("@target(org.springframework.stereotype.Service)")
    public void isService() {
    }
}
