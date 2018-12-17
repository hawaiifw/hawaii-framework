# Release Notes #

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
** Define a default filter order, right after Spring security
** All logging filters updated from downstream project
