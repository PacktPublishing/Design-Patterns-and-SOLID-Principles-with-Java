# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 1, Video 5

Changes since _Section 1_, _Video 4_.

* Introduce `Exporter` interface.
* Make `AbstractExporter` implement the `Exporter` interface.
* Add new enums to `ExportType`.
* Add `HtmlExporter` and `JsonExporter`.
* `calcWidths` method refactored and pulled up to `AbstractExporter` from `TxtExporter`.
* Update `Cli` to use `Exporter` type instead of `AbstractExporter`.
