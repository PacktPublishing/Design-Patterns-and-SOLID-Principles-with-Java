# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 3, Video 3

Changes since _Section 3_, _Video 2_.

* Introduced `ReportDelivery` interface as an example for the Strategy Pattern.
* Add dependency to `ReportDelivery` into `Cli` and `Web`.
* Used Dependency Inversion to allow replacing different report generation algorithms at runtime.

**Note**: at this point `ReportDelivery` didn't have any implementations, this phase is just for demonstration.
