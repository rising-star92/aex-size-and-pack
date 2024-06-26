package com.walmart.aex.sp.enums;

import java.util.List;
import java.util.Set;

public enum RunStatusCodeType {
    NOT_SENT_TO_ANALYTICS(0, "NOT SENT TO ANALYTICS"),
    SENT_TO_ANALYTICS(3, "SENT TO ANALYTICS"),
    ANALYTICS_RUN_COMPLETED(6, "ANALYTICS RUN COMPLETED"),
    N_JOBS_MSG_ERROR(10, "Fineline failed, please contact the support team"),
    INPUT_DATA_ERR_MSG(11, "Initial data sets should not be empty"),
    INPUT_DATA_ERR_ZERO_INITIAL_BUMPSET_MSG(12, "Fineline failed, please contact the support team"),
    NUMTOTALCCS_VALUE_ERROR_MSG(13, "Fineline failed, please contact the support team"),
    INITIAL_SET_CC_VALUE_ERROR_MSG(14, "Fineline has an initial set with less than 6 units. Please adjust units or consider combining colors."),
    BUMP_SET_CC_VALUE_ERROR_MSG(15, "Fineline has a bump set with less than 6 units. Please adjust units or consider combining colors."),
    DATA_VALIDATIONS_MSG(16, "Fineline failed, please contact the support team"),
    DUPLICATE_DATA_ERROR_MSG(17, "Duplicate data in initial data set, please contact the support team"),
    MODEL_OUTPUT_MSG(18, "Fineline failed, please contact the support team"),
    MODEL_OUTPUT_COLUMNS(19, "Fineline failed, please contact the support team"),
    SOLVER_NOT_SOLVE_ERROR_MSG(20, "Fineline failed, the max capacity is not enough to meet the pack requirements. Please increase max capacity"),
    TIMEOUT_ERROR(21, "Request failed due to system error, please retrigger Pack Optimization"),
    INTEGRATION_HUB_TECHNICAL_ERROR(22, "Request failed due to system error, please retrigger Pack Optimization"),
    MAX_PACK_CONFIG_ERROR(23, "No Solution: Review Max no of pack configurations. Need >#number"),
    COMMON_ERR_MSG(100, "Fineline failed, please contact the support team"),
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
            RunStatusCodeType.SOLVER_NOT_SOLVE_ERROR_MSG.getId(), RunStatusCodeType.COMMON_ERR_MSG.getId(), RunStatusCodeType.INTEGRATION_HUB_TECHNICAL_ERROR.getId(),
            RunStatusCodeType.TIMEOUT_ERROR.getId(),RunStatusCodeType.MAX_PACK_CONFIG_ERROR.getId());

    public static List<Integer> getPrefixEligibleRunStatusCodes() {
        return List.of(RunStatusCodeType.INITIAL_SET_CC_VALUE_ERROR_MSG.getId(),RunStatusCodeType.BUMP_SET_CC_VALUE_ERROR_MSG.getId());
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
