package com.walmart.aex.sp.enums;

public enum AppMessageText {
    RFA_NOT_AVAILABLE(300),
    RFA_CC_NOT_AVAILABLE(301),
    RFA_MISSING_FIXTURE(302),
    RFA_MISSING_COLOR_FAMILY(303),
    BQFP_MISSING_IS_DATA(300),
    BQFP_MISSING_IS_QUANTITIES(301),
    BQFP_MISSING_REPLENISHMENT_QUANTITIES(302),
    BQFP_MISSING_BUMPSET_QUANTITIES(303),
    BQFP_MISSING_BUMPSET_WEEKS(304);

    private final Integer id;

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
