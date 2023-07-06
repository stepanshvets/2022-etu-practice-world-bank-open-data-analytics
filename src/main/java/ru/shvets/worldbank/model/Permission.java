package ru.shvets.worldbank.model;

public enum Permission {
    WRITE("write"), READ("read");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
