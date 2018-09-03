# Release Notes #

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
