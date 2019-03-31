# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 2, Video 5

Replace hardwired singleton instance references and implement a simple DB-backed DAL for demonstration.

* Extract direct DAO instance references into fields in `Warehouse`.
* Create a DB-backed implementation of the `CustomerDao` using H2 in-memory database.
* Update `Warehouse` to use the `DbCustomerDao` instead of the `MemoryCustomerDao`.
* Update DAL interface definitions to allow throwing `WarehouseException`, update code to handle the checked exceptions on the right level.             
