# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 3, Video 2

Changes since _Section 3_, _Video 1_.

* Created `ReportGeneration` interface as an example strategy.
* Refactor `Warehouse` class to depend on the `ReportGeneration` interface instead of directly containing code related to report generation.
* Extract the report generation logic from the `Warehouse` class into the `DefaultReportGeneration` class which implements the strategy interface.
* Create a new implementation of the strategy in the `AlternativeReportGeneration` class.
* Update `Main` to depent on a client ID supplied as a command-line argument as an example to runtime algorithm selection.
