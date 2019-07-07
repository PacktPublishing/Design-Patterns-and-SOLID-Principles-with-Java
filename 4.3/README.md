# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 4, Video 3

Changes since _Section 4_, _Video 2_.

* Add `FULL_VERSION` system property to simulate full and trial application modes.
* Refactor the `Cli` and `Web` classes to depend on the `newExporter` method to instantiate exporters.
* Create `TrialCli` and `TrialWeb` subclasses that overwrites the `newExporter` method in the `Cli` and `Web`
classes respectively to demonstrate the possibilities of the Factory Method design pattern. 
* Update `Main` to instantiate the "regular" or the trial instances of the aforementioned classes based
on the value of the `FULL_VERSION` flag.

**Note**: changed how the HtmlEscaperOutputStream class is used - after export, instead before export.
