package com.walmart.aex.sp.enums;

import java.util.Set;
import java.util.stream.Stream;

public enum RunStatusCodeType {
    NOT_SENT_TO_ANALYTICS(0, "NOT SENT TO ANALYTICS"),
    SENT_TO_ANALYTICS(3, "SENT TO ANALYTICS"),
    ANALYTICS_RUN_COMPLETED(6, "ANALYTICS RUN COMPLETED"),
    N_JOBS_MSG_ERROR(10, "FINELINE FAILED"),
    INPUT_DATA_ERR_MSG(11, "INITIAL DS EMPTY"),
    INPUT_DATA_ERR_ZERO_INITIAL_BUMPSET_MSG(12, "FINELINE FAILED"),
    NUMTOTALCCS_VALUE_ERROR_MSG(13, "FINELINE FAILED"),
    INITIAL_SET_CC_VALUE_ERROR_MSG(14, "INITIAL SET LESS THAN 6 UNITS"),
    BUMP_SET_CC_VALUE_ERROR_MSG(15, "BUMP SET LESS THAN 6 UNITS"),
    DATA_VALIDATIONS_MSG(16, "FINELINE FAILED"),
    DUPLICATE_DATA_ERROR_MSG(17, "DUPLICATE DATA IN INITIAL DS"),
    MODEL_OUTPUT_MSG(18, "FINELINE FAILED"),
    MODEL_OUTPUT_COLUMNS(19, "FINELINE FAILED"),
    SOLVER_NOT_SOLVE_ERROR_MSG(20, "MAX CAPACITY NOT ENOUGH"),
    COMMON_ERR_MSG(100, "FINELINE FAILED"),
    ERROR(101, "ANALYTICS ERROR");

    private Integer id;
    private String description;

    private RunStatusCodeType(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public static final Set<Integer> ANALYTICS_ERRORS_LIST = Set.of(RunStatusCodeType.N_JOBS_MSG_ERROR.getId(), RunStatusCodeType.INPUT_DATA_ERR_MSG.getId(),
            RunStatusCodeType.INPUT_DATA_ERR_ZERO_INITIAL_BUMPSET_MSG.getId(), RunStatusCodeType.NUMTOTALCCS_VALUE_ERROR_MSG.getId(),
            RunStatusCodeType.INITIAL_SET_CC_VALUE_ERROR_MSG.getId(), RunStatusCodeType.BUMP_SET_CC_VALUE_ERROR_MSG.getId(),
            RunStatusCodeType.DATA_VALIDATIONS_MSG.getId(), RunStatusCodeType.DUPLICATE_DATA_ERROR_MSG.getId(),
            RunStatusCodeType.MODEL_OUTPUT_MSG.getId(), RunStatusCodeType.MODEL_OUTPUT_COLUMNS.getId(),
            RunStatusCodeType.SOLVER_NOT_SOLVE_ERROR_MSG.getId(), RunStatusCodeType.COMMON_ERR_MSG.getId());

    /**
     * @return the id
     */
    public final Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public final void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public final void setDescription(String description) {
        this.description = description;
    }

    public static String getRunStatusFromId(Integer id) {
        return Stream.of(values())
                .filter(e -> e.id.equals(id))
                .findFirst().map(RunStatusCodeType::getDescription).orElse(null);
    }
}
