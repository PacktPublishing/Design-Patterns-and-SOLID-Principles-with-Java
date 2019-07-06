# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 6, Video 5

Changes since _Section 6_, _Video 4_.

* Add `demo-func` Azure Function App project.
* Add `OrderDao.getOrder(int)` method, implement the interface method in subclasses - `DbOrderDao` and `MemoryDbDao`.  
* Add `Warehouse.get{Order,Product,Customer}(int)` methods.
* Move Gson dependency to `demo-core` and create reusable `Util.newGson` util method.
* Replace `com.mashape.unirest:unirest-java` dependency with `com.konghq:unirest-java` as the latter is maintained one. 

