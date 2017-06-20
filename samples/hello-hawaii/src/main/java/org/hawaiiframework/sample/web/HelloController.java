/*
 * Copyright 2015-2017 the original author or authors.
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

package org.hawaiiframework.sample.web;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.sample.service.HelloService;
import org.hawaiiframework.sample.service.Language;
import org.hawaiiframework.time.HawaiiTime;
import org.hawaiiframework.validation.ValidationError;
import org.hawaiiframework.validation.ValidationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Marcel Overdijk
 */
@RestController
@RequestMapping(path = "/api/hello")
public class HelloController {

    private static Logger logger = LoggerFactory.getLogger(HelloController.class);

    private final HelloService helloService;
    private final HawaiiTime hawaiiTime;

    @Autowired
    public HelloController(HelloService helloService, HawaiiTime hawaiiTime) {
        this.helloService = helloService;
        this.hawaiiTime = hawaiiTime;
    }

    @GetMapping(path = "/greet", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> greet(@RequestParam(required = false) String name, @RequestParam(required = false) String language) {

        logger.info("greet called with name: {}, language: {}", name, language);

        // Validate language
        if (StringUtils.isNotBlank(language)) {
            language = StringUtils.upperCase(language);
            if (!EnumUtils.isValidEnum(Language.class, language)) {
                throw new ValidationException(new ValidationError("language", "invalid"));
            }
        }

        // Create resource to be returned to client
        JSONObject resource = new JSONObject();
        resource.put("timestamp", hawaiiTime.zonedDateTime());
        resource.put("greeting", helloService.greet(name, EnumUtils.getEnum(Language.class, language)));

        return ResponseEntity.ok().body(resource);
    }
}
