package com.prm392_g1.sneakerhub.enums;

import lombok.Getter;

public enum RoleEnum {
    ADMIN("admin"),
    CUSTOMER("customer"),
    ;

    private final String value;

    public String getValue() {
        return value;
    }

    RoleEnum(String value) {
        this.value = value;
    }

    public static RoleEnum findByValue(String value) {
        if (value == null) return null;

        for (RoleEnum role : RoleEnum.values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }

        return null;
    }
}
