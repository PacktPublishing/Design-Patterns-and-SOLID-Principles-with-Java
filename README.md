# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 2, Video 1

Changes since Section 1, Video 5.

Add a new web interface, implement add product/order functions.
After starting the application the web interface is available at [`http://localhost:8080`](http://localhost:8080).

* Updated `pom.xml` with Spark dependencies to implement the simple web interface.
* Added `simplelogger.properties` file to the classpath which disables verbose Spark logging.
* Added dynamic Velocity templates (`*.html.vm` files) which provides the dynamic HTML templates for the web interface.
* Added a `Web` class that starts up Spark and configures request handlers, routes request to the necessary `Warehouse` methods.
* Updated `Main` to start both the `Cli` and `Web` interfaces.
* Updated `Warehouse` and implement `addProduct` and `addOrder` methods for demo purposes.
* Updated `Warehouse` - `getOrders`, `getCustomers`, etc. return objects sorted in a sane way.
* Updated `Cli` - wired `addProduct` and `addOrder` methods of the `Warehouse` class.
