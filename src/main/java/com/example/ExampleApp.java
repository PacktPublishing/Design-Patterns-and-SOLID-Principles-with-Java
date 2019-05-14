package com.example;

import com.example.warehouse.DependencyFactory;

public final class ExampleApp implements Runnable {

    private final DependencyFactory dependencyFactory;

    ExampleApp(DependencyFactory dependencyFactory) {
        this.dependencyFactory = dependencyFactory;
    }

    @Override
    public void run() {
        // INFO: uses dependencyFactory.newExporter(...) only.
    }
}
