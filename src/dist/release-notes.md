# Release Notes #

### 6.0.0.M3-SNAPSHOT
* Suppress `form-data/multipart` request logging.
* Made separate ResponseEntityExceptionHandler for Spring Security exceptions.

## 6.0.0.M1
* Use Spring Framework 6.0.0
* Use Spring Boot 3.0.0

## 3.0.0.M24
* Configure field names for password masking (instead of just hard coded 'password').
* Added business transaction filters, like normal transaction filters. A business transaction is more like a session and spans multiple transactions.
* Moved HTTP client header providers for business transaction id and transaction id into the logging library (from async).

## 3.0.0.M23
* Fixed issue with application configuration for client IP logging.
* Use Spring Boot 2.4.1 by default.

## 3.0.0.M22
*  Tag input and output of calls (incoming / outbound) in Kibana fields instead of log message body.

## 3.0.0.M21
* Added Open Telemetry trace id to a servlet response.
* Added OIDC fields to Kibana logging.
* Added extra fields to Kibana logging.

## 3.0.0.M19
* Always log that a response was received for an HTTP call done to another service.
* Adjusted log message and added stacktrace when receiving an IO exception for HTTP calls to other services. 

## 3.0.0.M18
* Enable logging by default, even for missing properties.
* Added software version kibana log field & logger.
* Do not require Apache CXF to be present on the classpath for logging to work.
* Added wrapper around scheduled tasks to add a kibana tx_id.
* Fixed NPE in TaskRemoveStrategy.
* Added GuardedMethod, to allow different threads to have only one thread active for a method / set of methods.

## 3.0.0.M14
* Added opentracing
* Refactored SQL logging
* Added logging of SOAP requests and responses

## 3.0.0.M13
* Make the fallbackToFile configuration apply to both request and response

## 3.0.0.M12
* Move hamcrest, jasypt and bouncycastle to api dependencies

## 3.0.0.M11
* Upgrade to Gradle version 6.2.2
* Apply `java-library` plugin for framework projects, and the `java` plugin for sample projects
* Replace deprecated `compile` configurations with either `implementation` or `api` to better control transitive dependencies
* Upgrade to Spring Boot version 2.2.5.RELEASE

## 3.0.0.M10
* Log call duration in separate log field for elastic search.

## 3.0.0.M9
* Updated to Spring Boot 2.1.7.RELEASE
* Logging improvements (#44)
* Added `timeOutInMinutes` to `HawaiiRedisCacheBuilder` (#45)
* Renamed Async related classes and methods (#46)
  This causes breaking changes with the following upgrade instructions:
  * `VoidResult` → `Void`
  * `AsyncUtil.invokeAndLogError()` → `AsyncUtil.invoke()`
  * `AsyncUtil.expectErrorInTimeoutOrStopWaiting()` → `AsyncUtil.waitForCompletion()`

## 3.0.0.M8
* Updated to Spring Boot 2.1.6.RELEASE + other dependencies
* Added hawaii-security module containing the HawaiiTokenService
* Added Async utility classes and HawaiiQueryExecutionListener

## 3.0.0.M7
* Remove ErrorMessageResponseEnricher from the default configuration to prevent information leakage

## 3.0.0.M6
* Updated to Spring Boot 2.1.4.RELEASE + other dependencies
* Added HexEncoder
* Added HostResolver

## 3.0.0.M5
* Updated to Spring Boot 2.1.3.RELEASE + other dependencies

## 3.0.0.M4
* Allow Async annotation without an executor name
* Use delegating executor for the default executor
* Redis cache implementation
* Added strategy how to handle null lists in abstract model converter
* Refactored kibana log field names from '.' to '_'

## 3.0.0.M3
* Change .yml files to snake case
* Documentation updates
* Added new logging filter that inserts the controller and the method name into the logging context
* Only use MdcContext indirectly, through KibanaLogContext
* Change dependendies of ValidationErrorResponseEnricher and HawaiiResponseEntityExceptionHandler 
  to interfaces instead of the actual implementation

## 3.0.0.M2
* Clear the MDC after test
* More unit tests
* License header fixes
* Disable Asciidoc PDF generation (prevented building on Windows)
* Updated to Spring Boot 2.1.0.RELEASE + other dependencies
* Build on JDK 11

## 3.0.0.M1

### General
* Next major version of Hawaii Framework.
* Upgrade to Java 10
* Upgrade to Spring 5
* Upgrade to Spring boot 2

## 2.0.0.M16

### Hawaii logging
* Make Kibana logging extensible in client projects.
* Make Hawaii logging usable without Spring security

## 2.0.0.M15

### Hawaii framework
* Updated Spring Platform Brussels-SR11

### Hawaii framework core
* Make sure ErrorResponseEnrichers can be removed by class as well

## 2.0.0.M14

### Hawaii logging
* Do not log the request content in case of a file upload
* Create separate beans for individual logging filters
* Various improvements
  * Define a default filter order, right after Spring security
  * All logging filters updated from downstream project
