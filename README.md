# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 4, Video 4

Changes since _Section 4_, _Video 3_.

* Replace Factory Method pattern usage with Abstract Factory patter usage.
* Eliminate full and trial versions of the `Cli` and `Web` class, create a shared `ExporterFactory` instead.
* Create full and trial versions of the `ExporterFactory` - code reuse.
* Refactor `Main` to instantiate a specific `ExporterFactor` and supply that to `Cli` and `Web`.
