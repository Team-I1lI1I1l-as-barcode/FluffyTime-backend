package com.fluffytime.domain.user.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleName {
    ROLE_ADMIN("ROLE_ADMIN","ADMIN"),
    ROLE_USER("ROLE_USER","USER");

    private final String name;
    private final String noneHeaderName;
}
