# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 2, Video 4

Create a Data Access Layer package and extracted interfaces from previous class definitions.

* Extract data access functionality from the `Warehouse` class.
* Separate business logic and data access into two layers.
    1. `Warehouse` receives user input, validates it and creates domain objects and uses DAL to store data.
    1. DAL stores data and generates unique IDs for objects.
* To facilitate separation of concerns domain objects like `Order`, `Customer`, etc. are updated.
    1. Copy-constructors are introduced for robustness and "security" (_defensive copying_).
    1. Domain objects are created without IDs in the business layer - the DAL assigns unique IDs.
* Create a Data Access Layer package called `dal` from the extracted code with classes corresponding to various domain objects (e.g. `CustomerDao`, `ProductDao`, etc.).
* Create interfaces by extracting data access functionality from data access objects (DAOs).
**Note**: previous DAO classes turned to _interfaces_.
* Rename earlier DAO objects, like `OrderDao` class to `MemoryOrderDao` and make the new class implement the DAO interface `OrderDao`. 
