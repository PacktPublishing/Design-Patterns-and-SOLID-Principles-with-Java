package com.example.warehouse;

import com.example.warehouse.dal.OrderDao;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class AlternativeReportGeneration implements ReportGeneration {

    private final OrderDao orderDao;

    public AlternativeReportGeneration(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Report generateReport(Report.Type type) throws WarehouseException {
        checkReportType(type);
        Report report = new Report();
        report.addLabel("Date");
        report.addLabel("Total products");
        report.addLabel("Total revenue");
        orderDao.getOrders()
            .stream()
            .sorted()
            .collect(groupingBy(Order::getDate, LinkedHashMap::new, toList()))
            .forEach((date, orders) -> report.addRecord(
                new Report.Field(Report.DataType.DATE, date),
                new Report.Field(Report.DataType.NUMBER, orders
                    .stream()
                    .sorted()
                    .map(Order::getQuantities)
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .mapToInt(Integer::intValue)
                    .sum()),
                new Report.Field(Report.DataType.NUMBER, orders
                    .stream()
                    .sorted()
                    .mapToInt(Order::getTotalPrice)
                    .sum())));
        return report;
    }

    private void checkReportType(Report.Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Report type cannot be null.");
        }
        if (type != Report.Type.DAILY_REVENUE) {
            throw new UnsupportedOperationException(String.format("Report type: %s not yet implemented.", type));
        }
    }
}
