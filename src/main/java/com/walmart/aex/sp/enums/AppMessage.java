package com.walmart.aex.sp.enums;

public enum AppMessage {
    SIZE_PROFILE_PCT_NOT100(152, 152),
    SIZE_PROFILE_PCT_NOT100_CC_LEVEL(153, SIZE_PROFILE_PCT_NOT100.getId()),
    BQFP_MESSAGE(160, 160),
    BQFP_MISSING_IS_DATA(161, BQFP_MESSAGE.getId()),
    BQFP_MISSING_IS_UNITS(162, BQFP_MESSAGE.getId()),
    BQFP_MISSING_REPLN_UNITS(163, BQFP_MESSAGE.getId()),
    BQFP_MISSING_BS_UNITS(164, BQFP_MESSAGE.getId()),
    BQFP_MISSING_BS_WEEKS(165, BQFP_MESSAGE.getId()),
    BQFP_BS_NEGATIVE_UNITS(166, BQFP_MESSAGE.getId()),
    BQFP_IS_NEGATIVE_UNITS(167, BQFP_MESSAGE.getId()),
    BQFP_REPLN_NEGATIVE_UNITS(168, BQFP_MESSAGE.getId()),
    RFA_MESSAGE(170, 170),
    RFA_NOT_AVAILABLE(171, RFA_MESSAGE.getId()),
    RFA_CC_NOT_AVAILABLE(172, RFA_MESSAGE.getId()),
    RFA_MISSING_FIXTURE(173, RFA_MESSAGE.getId()),
    RFA_MISSING_COLOR_FAMILY(174, RFA_MESSAGE.getId()),
    RULE_IS_ONE_UNIT_PER_STORE_APPLIED(210, 210),
    RULE_IS_ONE_UNIT_PER_STORE_SIZE_APPLIED(211, RULE_IS_ONE_UNIT_PER_STORE_APPLIED.getId()),
    RULE_ADJUST_REPLN_ONE_UNIT_PER_STORE_APPLIED(212, 212),
    RULE_ADJUST_REPLN_ONE_UNIT_PER_STORE_SIZE_APPLIED(213, RULE_ADJUST_REPLN_ONE_UNIT_PER_STORE_APPLIED.getId()),
    RULE_IS_REPLN_ITM_PC_APPLIED(220, 220),
    RULE_IS_REPLN_ITM_PC_SIZE_APPLIED(221, RULE_IS_REPLN_ITM_PC_APPLIED.getId()),
    RULE_ADJUST_MIN_REPLN_THRESHOLD_APPLIED(222, 222),
    RULE_ADJUST_MIN_REPLN_THRESHOLD_SIZE_APPLIED(223, RULE_ADJUST_MIN_REPLN_THRESHOLD_APPLIED.getId());

    private final Integer id;
    private final Integer parentId;

    AppMessage(Integer id, Integer parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    /**
     * @return the id
     */
    public final Integer getId() {
        return id;
    }

    /**
     * @return the id
     */
    public final Integer getParentId() {
        return parentId;
    }

}
