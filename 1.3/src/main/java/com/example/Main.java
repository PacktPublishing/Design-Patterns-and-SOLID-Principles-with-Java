package com.example;

import com.example.cli.Cli;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        new Cli(Arrays.asList(args)).run();
    }
}
