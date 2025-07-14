package com.prm392_g1.sneakerhub.enums;

public enum ColourEnum {
    BLACK("Black"),
    WHITE("White"),
    RED("Red"),
    BLUE("Blue"),
    GREEN("Green"),
    YELLOW("Yellow"),
    ORANGE("Orange"),
    PURPLE("Purple"),
    PINK("Pink"),
    BROWN("Brown"),
    GRAY("Gray"),
    NAVY("Navy"),
    MAROON("Maroon"),
    LIME("Lime"),
    TEAL("Teal"),
    SILVER("Silver"),
    GOLD("Gold");

    private final String displayValue;

    ColourEnum(String displayValue) {
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
        ColourEnum[] values = values();
        String[] displayValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            displayValues[i] = values[i].getDisplayValue();
        }
        return displayValues;
    }

    public static ColourEnum fromDisplayValue(String displayValue) {
        for (ColourEnum colour : values()) {
            if (colour.getDisplayValue().equals(displayValue)) {
                return colour;
            }
        }
        throw new IllegalArgumentException("Invalid colour: " + displayValue);
    }
}
