package com.walmart.aex.sp.enums;

public enum AppMessageType {
    INFORMATION(0, "Informational message"),
    WARNING(2, "Warning message"),
    ERROR(3, "Error message");

    private final Integer id;
    private final String description;

    AppMessageType(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * @return the id
     */
    public final Integer getId() {
        return id;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

}
