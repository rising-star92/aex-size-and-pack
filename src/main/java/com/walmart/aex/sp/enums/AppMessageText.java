package com.walmart.aex.sp.enums;

public enum AppMessageText {
    RFA_MISSING_DATA(100),
    BQFP_MISSING_IS_DATA(200),
    BQFP_MISSING_IS_QUANTITIES(201),
    BQFP_MISSING_REPLENISHMENT_QUANTITIES(202),
    BQFP_MISSING_BUMPSET_QUANTITIES(203),
    BQFP_MISSING_BUMPSET_WEEKS(204);

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
