package com.example.languagelearningapp;

import java.io.Serializable;

public class Entity implements Serializable {
    private String property1;
    private String property2;
    private String description;

    // Add default constructor
    public Entity() {
    }

    public Entity(String property1, String property2, String description) {
        this.property1 = property1;
        this.property2 = property2;
        this.description = description;
    }

    // Add getters and setters
    public String getProperty1() {
        return property1 != null ? property1 : "";
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    public String getProperty2() {
        return property2 != null ? property2 : "";
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }
}