package com.walmart.aex.sp.enums;

public enum AppMessageText {
    RFA_MISSING_DATA(100);

    private Integer id;

    AppMessageText(Integer id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public final Integer getId() {
        return id;
    }
}
