package com.example;

import com.example.cli.Cli;
import com.example.web.Web;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        new Web(List.of(args)).run();
        new Cli(List.of(args)).run();
        // INFO: Needed because when Cli exists the Web
        // interface's thread will keep the app hanging.
        System.exit(0);
    }
}
