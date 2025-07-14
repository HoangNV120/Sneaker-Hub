package com.prm392_g1.sneakerhub.enums;

public enum SizeEnum {
    SIZE_35("35"),
    SIZE_36("36"),
    SIZE_37("37"),
    SIZE_38("38"),
    SIZE_39("39"),
    SIZE_40("40"),
    SIZE_41("41"),
    SIZE_42("42"),
    SIZE_43("43"),
    SIZE_44("44"),
    SIZE_45("45"),
    SIZE_46("46");

    private final String displayValue;

    SizeEnum(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    @Override
    public String toString() {
        return displayValue;
    }

    public static String[] getDisplayValues() {
        SizeEnum[] values = values();
        String[] displayValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            displayValues[i] = values[i].getDisplayValue();
        }
        return displayValues;
    }

    public static SizeEnum fromDisplayValue(String displayValue) {
        for (SizeEnum size : values()) {
            if (size.getDisplayValue().equals(displayValue)) {
                return size;
            }
        }
        throw new IllegalArgumentException("Invalid size: " + displayValue);
    }
}
