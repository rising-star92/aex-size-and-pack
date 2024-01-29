package com.walmart.aex.sp.enums;

public enum AppMessageType {
    INFORMATION(0, "INFORMATIONAL"),
    WARNING(2, "WARNING"),
    ERROR(3, "ERROR");

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
