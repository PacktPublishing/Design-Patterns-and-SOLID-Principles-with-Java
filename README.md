# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 6, Video 4

Changes since _Section 6_, _Video 3_.

* Add `demo-backend` project and new `Backend` class.
* Add new factory methods to `Warehouses` - `newFrontendWarehouse` and `newBackendWarehouse`.
* Refactor `App` and add new abstract method `getWarehouse(int)`.
Update `Cli`, `Web` and `Backend` according to this.
* Create `AbstractRestDao` and `ProductRestDao`, refactor `CustomerRestDao` to extend `AbstractRestDao`.
* Fix a few miscellaneous problems (`Backend` getting NPE during exception translation, not handling `500` errors in `AbstractRestDao.getArray`).
