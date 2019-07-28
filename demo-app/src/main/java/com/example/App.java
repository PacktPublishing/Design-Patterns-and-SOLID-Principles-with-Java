package com.example;

import com.example.warehouse.*;
import com.example.warehouse.delivery.DirectoryReportDelivery;
import com.example.warehouse.delivery.EmailReportDelivery;
import com.example.warehouse.delivery.NoReportDelivery;
import com.example.warehouse.delivery.ReportDelivery;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.decorators.Decorators;

import javax.mail.internet.AddressException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.System.*;

public abstract class App implements Runnable {

    private static final String DESTINATION_ADDRESS = getenv()
        .getOrDefault("DESTINATION_ADDRESS", "destination@demo.com");

    private static final String DESTINATION_DIRECTORY = getenv()
        .getOrDefault("DESTINATION_DIRECTORY", ".");

    private static final CircuitBreakerConfig CBC = CircuitBreakerConfig.custom()
        .ringBufferSizeInClosedState(3)
        .ringBufferSizeInHalfOpenState(3)
        .failureRateThreshold(50)
        .waitDurationInOpenState(Duration.ofSeconds(10))
        .build();

    private static final CircuitBreaker CIRCUIT_BREAKER = CircuitBreaker.of("customers", CBC);

    protected final DependencyFactory dependencyFactory;

    protected Warehouse warehouse;
    protected List<ReportDelivery> reportDeliveries;
    protected ReportDelivery activeReportDelivery;

    private final int clientId;

    protected App() {
        this.clientId = getClientId();
        this.dependencyFactory = new DynamicDependencyFactory();

        warehouse = getWarehouse(clientId);

        try {
            reportDeliveries = createReportDeliveries(clientId);
        } catch (AddressException ex) {
            err.println("Wrong email address:" + ex.getMessage());
            exit(1);
            return;
        }

        activeReportDelivery = reportDeliveries.get(0);
    }

    protected abstract Warehouse getWarehouse(int clientId);

    protected Collection<Customer> getCustomers() throws WarehouseException {
        try {
            return Decorators.ofCheckedSupplier(warehouse::getCustomers)
                .withCircuitBreaker(CIRCUIT_BREAKER)
                .get();
        } catch (WarehouseException | CallNotPermittedException ex) {
            throw ex;
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    private static List<ReportDelivery> createReportDeliveries(int clientId) throws AddressException {
        List<ReportDelivery> result = new ArrayList<>();
        result.add(new NoReportDelivery());
        if (clientId == 1) {
            result.add(new EmailReportDelivery(DESTINATION_ADDRESS));
            result.add(new DirectoryReportDelivery(DESTINATION_DIRECTORY));
        } else {
            result.add(new DirectoryReportDelivery(DESTINATION_DIRECTORY));
        }
        return result;
    }

    private static int getClientId() {
        String value = getenv("CLIENT_ID");
        if (value == null || value.isBlank()) {
            throw newIllegalClientId(value);
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            throw newIllegalClientId(value);
        }
    }

    private static IllegalStateException newIllegalClientId(String value) {
        return new IllegalStateException(String.format("Illegal client ID: %s. It must be an integer.", value));
    }
}
