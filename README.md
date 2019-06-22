# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 5, Video 5

Changes since _Section 5_, Video 4.

* Remove `ExternalCustomerService` and related classes.
* Add new submodule `demo-rest`.
* Split database (and `init.sql` scripts) in order to separate customer related info from other data.  
* Move `DbCustomerDao` implementation to `demo-rest`.
* Create `Rest` class that acts as Microservice Facade between the main application and between different subsystems
providing customer data the app needs.
* Create `RestCustomerDao`, a new `CustomerDao` implementation that fetches customer data via HTTP calls to sent to the
Microservice Facade. 
