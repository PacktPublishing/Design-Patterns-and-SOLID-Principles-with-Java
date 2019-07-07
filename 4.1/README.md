# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 4, Video 1

Changes since _Section 3_, _Video 5_.

* Moved classes related to `ReportDelivery` into package `delivery`.
* Create `AbstractReportDelivery` to demonstrate how to mix the Strategy and Template Method patterns.
* Refactor `ReportDelivery` implementations to depend on `AbstractReportDelivery`.
* Add database-based DAO implementations - made the application use this by default (updated `init.sql` along the way,
it contains the same set of date as the `.csv` files).
