# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 1, Video 4

Changes since _Section 1_, _Video 3_.

* Add `Exporter` interface and an enum inside the interface called `ExportType`.
* Add `AbstractExporter` abstract class utilizing the Template Method pattern.
* Refactor and rename the former `Exporter` to `TxtExporter` - extends the `AbstractExporter`.
* Create new `CsvExporter` class that also extends the `AbstractExporter`.
* Update the `Cli` class and add a new menu option to export the daily revenue report to CSV format also.
* In the `Cli` instantiate the exporter for the format selected by the user.
