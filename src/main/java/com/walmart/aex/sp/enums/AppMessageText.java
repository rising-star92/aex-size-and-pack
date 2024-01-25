package com.walmart.aex.sp.enums;

public enum AppMessageText {
    BQFP_MISSING_IS_DATA(300),
    BQFP_MISSING_IS_QUANTITIES(301),
    BQFP_MISSING_REPLENISHMENT_QUANTITIES(302),
    BQFP_MISSING_BUMPSET_QUANTITIES(303),
    BQFP_MISSING_BUMPSET_WEEKS(304),
    RFA_NOT_AVAILABLE(310),
    RFA_CC_NOT_AVAILABLE(311),
    RFA_MISSING_FIXTURE(312),
    RFA_MISSING_COLOR_FAMILY(313),
    INITIALSET_ONE_UNIT_PER_STORE_APPLIED(210),
    ADJUST_REPLN_FOR_ONE_UNIT_PER_STORE_APPLIED(211);

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
