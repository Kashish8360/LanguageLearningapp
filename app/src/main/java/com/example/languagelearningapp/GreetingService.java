package com.example.languagelearningapp;

import javax.inject.Inject;

public class GreetingService {

    @Inject
    public GreetingService() {
        // Default constructor
    }

    public String getGreeting() {
        return "Hello from Hilt in Java!";
    }
}
