# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 6, Video 1

Changes since _Section 5_, _Video 5_.
 
* Fix path parameter handling in `Rest` line _59_.
* Fix non-existent reference in SQL query in `DbOrderDao` line _88_.
Forgot to replace `c.id AS customer_id` with `o.customer_id AS customer_id`

**Note**: in _Video 5.5_ the two lines referred to above was displayed in their _unfixed_ states.
Because of these if you try to list orders in the main app you'll receive an error while on branch `5.5.x`.

