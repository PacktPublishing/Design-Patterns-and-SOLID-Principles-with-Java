# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 5, Video 1

Changes since _Section 4_, _Video 5_.

* Refactor into a multi-module Maven project (move a lot of files around).
* There are 3 modules
    * _demo-core_ contains classes common to other modules.
    * _demo-app_ contains the main class and code available in the trial version of the application.
    * _demo-app-full_ contains code that is only available when running the app in full version.
* Create a new `DependencyFactory` interface implementation that uses Reflection to find classes implementing the
same interface on the classpath. It creates an instance of the found class and delegates to this during runtime.
 
