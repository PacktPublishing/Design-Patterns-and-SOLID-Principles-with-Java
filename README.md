# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 1, Video 2

Changes since _Section 1_, _Video 1_.

* Add starter sample application code.
* Add packages `warehouse` and `cli`.
* `warehouse` contains the classes making up the API to a simple warehouse management software that read/writes data to CSV files. With it a user
    * can manage products, customers and their orders
    * and create/export simple reports.
* `cli` contains a single class `Cli` which displays the menu, handles user input and delegates call to a `Warehouse` instance.
* Data is stored in the root of the project's repository in the following files:
    * `products.csv`: list of available products, their IDs and prices.
    * `inventory.csv`: available quantity for products.
    * `customers.csv`: list of customers, their IDs and names.
    * `orders.csv`: list of previous and future orders, their IDs, date, the customer who ordered the products and the list of products ordered.
* **Not every functionality is implemented** at this point, e.g. only the listing of products, customers, etc. is possible, adding, updating, etc. isn't.
