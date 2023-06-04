package ru.shvets.worldbank.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    ADMIN(Set.of(Permission.READ, Permission.WRITE, Permission.MANAGE)),
    MANAGER(Set.of(Permission.READ, Permission.WRITE)),
    USER(Set.of(Permission.READ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return this.permissions.stream().map(authority ->
                new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toSet());
    }
}
