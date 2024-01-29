package com.walmart.aex.sp.enums;

public enum AppMessageText {
    BQFP_MISSING_IS_DATA(160),
    BQFP_MISSING_IS_QUANTITIES(161),
    BQFP_MISSING_REPLENISHMENT_QUANTITIES(162),
    BQFP_MISSING_BUMPSET_QUANTITIES(163),
    BQFP_MISSING_BUMPSET_WEEKS(164),
    RFA_NOT_AVAILABLE(170),
    RFA_CC_NOT_AVAILABLE(171),
    RFA_MISSING_FIXTURE(172),
    RFA_MISSING_COLOR_FAMILY(173),
    RULE_INITIALSET_ONE_UNIT_PER_STORE_APPLIED(210),
    RULE_ADJUST_REPLN_FOR_ONE_UNIT_PER_STORE_APPLIED(211),
    RULE_MIN_INITIALSET_THRESHOLD_APPLIED(212),
    RULE_REPLN_UNITS_MOVED_TO_INITIAL_SET_APPLIED(213);

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
