package com.walmart.aex.sp.enums;

import java.util.stream.Stream;

public enum Action {
    ADD("Add"),
    DELETE("Delete");

    private String description;

    private Action(String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    public static String getEnumValue(String description) {
        return Stream.of(values())
                .filter(e -> e.description.equalsIgnoreCase(description))
                .findFirst().map(Action::getDescription).orElse(null);
    }

}
