# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 4, Video 4

Changes since _Section 4_, _Video 3_.

* Replace Factory Method pattern usage with Abstract Factory patter usage.
* Eliminate full and trial versions of the `Cli` and `Web` class, create a shared `ExporterFactory` instead.
* Create full and trial versions of the `ExporterFactory` - code reuse.
* Refactor `Main` to instantiate a specific `ExporterFactor` and supply that to `Cli` and `Web`.

**Note**: updated and refactored a bit how `Report` objects are created - previously values were stored in strings,
now they are stored in `Field` objects which encapsulates the data and the data type as well in order that chart plotting
could be implemented in a "sane" way.  
