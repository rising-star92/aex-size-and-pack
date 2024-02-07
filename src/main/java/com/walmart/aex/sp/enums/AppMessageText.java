package com.walmart.aex.sp.enums;

import java.util.Set;

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
    RULE_REPLN_UNITS_MOVED_TO_INITIAL_SET_APPLIED(213),
    SIZE_PROFILE_PCT_NOT100_CC_LEVEL(214),

    //hierarchy level messages
    SIZE_PROFILE_PCT_NOT100(300),
    BQFP_MESSAGE(301),
    RFA_MESSAGE(302),
    RULE_IS_ONE_UNIT_PER_STORE_APPLIED(303),
    RULE_ADJUST_REPLN_ONE_UNIT_PER_STORE_APPLIED(304),

    RULE_IS_REPLN_ITM_PC_APPLIED(305),
    RULE_ADJUST_MIN_REPLN_THRESHOLD_APPLIED(306) ;

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

    public static final Set<Integer> BQFP_ERRORS_LIST = Set.of(AppMessageText.BQFP_MISSING_IS_DATA.getId(), AppMessageText.BQFP_MISSING_IS_QUANTITIES.getId(),
            AppMessageText.BQFP_MISSING_REPLENISHMENT_QUANTITIES.getId(), AppMessageText.BQFP_MISSING_BUMPSET_QUANTITIES.getId(),
            AppMessageText.BQFP_MISSING_BUMPSET_WEEKS.getId());

    public static final Set<Integer> RFA_ERRORS_LIST = Set.of(AppMessageText.RFA_NOT_AVAILABLE.getId(), AppMessageText.RFA_CC_NOT_AVAILABLE.getId(),
            AppMessageText.RFA_MISSING_FIXTURE.getId(), AppMessageText.RFA_MISSING_COLOR_FAMILY.getId());

}
