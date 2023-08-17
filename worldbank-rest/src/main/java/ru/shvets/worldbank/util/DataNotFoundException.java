package ru.shvets.worldbank.util;

public class DataNotFoundException extends  RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}
