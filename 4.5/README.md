# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 4, Video 5

Changes since _Section 4_, _Video 4_.

* Apply the Abstract Factory pattern over `ChartPlotter` - create `ChartPlotterFactory` and related
implementations.
* Create a combined interface `DependencyFactory` - extends `ExporterFactory` and `ChartPlotterFactory`.
* Update `Cli` and `Web` to depend on a single `DependencyFactory` object instead of separate exporter and
plotter instances.
