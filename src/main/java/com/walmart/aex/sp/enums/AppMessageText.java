package com.walmart.aex.sp.enums;

public enum AppMessageText {
    BQFP_MISSING_IS_DATA(100),
    BQFP_MISSING_IS_QUANTITIES(101),
    BQFP_MISSING_REPLENISHMENT_QUANTITIES(102),
    BQFP_MISSING_BUMPSET_QUANTITIES(103),
    BQFP_MISSING_BUMPSET_WEEKS(104),
    RFA_NOT_AVAILABLE(110),
    RFA_CC_NOT_AVAILABLE(111),
    RFA_MISSING_FIXTURE(112),
    RFA_MISSING_COLOR_FAMILY(113),
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
