# Release Notes #

## 2.0.0.M19
* Issue releasing 2.0.0.M17|M18

## 2.0.0.M18
* Issue releasing 2.0.0.M17

## 2.0.0.M17
* Remove ErrorMessageResponseEnricher from the default configuration to prevent information leakage

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
