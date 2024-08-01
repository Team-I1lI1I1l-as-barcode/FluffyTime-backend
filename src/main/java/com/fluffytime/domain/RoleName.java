package com.fluffytime.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleName {
    ROLE_ADMIN("ADMIN"),
    ROLE_USER("USER");

    private final String name;
}
