hawaii-core
    - org.hawaiiframework.aspect -> deprecated?
    - org.hawaiiframework.converter -> deprecated?
    - org.hawaiiframework.crypto -> where used?
    - org.hawaiiframework.util.codec -> deprecated?
    - org.hawaiiframework.util.tuple -> deprecated?
    - org.hawaiiframework.util.StackedHashMap -> deprecated?
    - removed json styling, now default of camel-case is used
    - added environment crypto via `hwaii.crypto.{enabled=true,init}`. Init can be set to the "known default env var"

hawaii-logging:
    - removed opentracing
    - removed own kibana logback appender
        Use kibana logback append from elastic instead!
    - added path voter and `hawaii.logging.excluded-paths` with default of `/actuator/*`
    - added content type voter with defaults:
        - application/json
        - application/graphql+json,
        - application/vnd.spring-boot.actuator.v1+json
        - application/vnd.spring-boot.actuator.v3+json
        - application/vnd.spring-cloud.config-server.v2+json,
        - multipart/form-data
        - text/plain
        - text/xml
    - renamed hawaii.logging.open-telemetry-response -> hawaii.logging.open-telemetry-tracing-response
    - added hawaii.logging.micrometer-tracing-response

hawaii-cache
    no changes

hawaii-async    
    - removed opentracing
    - HawaiiAsyncUtil -> AsyncUtil

hawaii-security:
    deprecated
    removed

hawaii-test:
    deprecated
    removed

hawaii-autoconfigure
    - removed opentracing


merge hawaii-framework-incubating
    - JavaxValidationValidator -> JakartaValidationValidator

gw :hawaii-async:dependencies > tmp/hawaii-async-deps.txt
gw :hawaii-autoconfigure:dependencies > tmp/hawaii-autoconfigure-deps.txt
gw :hawaii-cache:dependencies > tmp/hawaii-cache-deps.txt
gw :hawaii-core:dependencies > tmp/hawaii-core-deps.txt
gw :hawaii-logging:dependencies > tmp/hawaii-logging-deps.txt
gw :hawaii-starter:dependencies > tmp/hawaii-starter-deps.txt
gw :hawaii-starter-async:dependencies > tmp/hawaii-starter-async-deps.txt
gw :hawaii-starter-boot:dependencies > tmp/hawaii-starter-boot-deps.txt
gw :hawaii-starter-cache:dependencies > tmp/hawaii-starter-cache-deps.txt
gw :hawaii-starter-logging:dependencies > tmp/hawaii-starter-logging-deps.txt
gw :hawaii-starter-rest:dependencies > tmp/hawaii-starter-rest-deps.txt

---
TODO: 
- New build @ Azure
- Publish artifacts to mvn central
