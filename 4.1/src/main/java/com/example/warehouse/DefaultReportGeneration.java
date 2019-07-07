package com.example.warehouse;

import com.example.warehouse.dal.OrderDao;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class DefaultReportGeneration implements ReportGeneration {

    private final OrderDao orderDao;

    public DefaultReportGeneration(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Report generateReport(Report.Type type) throws WarehouseException {
        checkReportType(type);
        Report report = new Report();
        report.addLabel("Date");
        report.addLabel("Total revenue");
        orderDao.getOrders()
            .stream()
            .filter(o -> !o.isPending())
            .sorted()
            .collect(groupingBy(Order::getDate, LinkedHashMap::new, summingInt(Order::getTotalPrice)))
            .forEach((date, totalRevenue) -> report.addRecord(Arrays.asList(date, totalRevenue)));
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
