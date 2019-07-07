# Design Patterns Warehouse Demo

## About

Video course companion code.

## Section 3, Video 4

Changes since _Section 3_, _Video 3_.

* Update the `ReportDelivery` interface to be usable - the earlier version was just a "sketch".
* Create an implementation of the `ReportDelivery` strategy called `EmailReportDelivery`.
* Use the `EmailReportDelivery` as the concrete dependency for the `Cli` and `Web` classes.
* Update Maven dependencies to include `javax.mail` used to send emails.
* Use the actual `ReportDelivery` dependency inside `Cli` and `Web` "correctly" - handling possible exceptions.
