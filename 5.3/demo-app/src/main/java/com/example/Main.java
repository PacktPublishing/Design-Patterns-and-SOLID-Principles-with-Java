package com.example;

import com.example.warehouse.DependencyFactory;
import com.example.warehouse.DynamicDependencyFactory;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        DependencyFactory dependencyFactory = new DynamicDependencyFactory();
        new App(List.of(args), dependencyFactory).run();
    }
}
