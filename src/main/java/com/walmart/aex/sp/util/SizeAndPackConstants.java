package com.walmart.aex.sp.util;

import java.util.Arrays;
import java.util.List;

public class SizeAndPackConstants {

    private SizeAndPackConstants() {
    }

    public static final Integer DEFAULT_REPL_ITEM_PIECE_RULE = 2;
    public static final Integer DEFAULT_MIN_REPL_ITEM_UNITS = 2500;
    public static final String SUCCESS_STATUS = "Success";
    public static final String FAILED_STATUS = "Failed";
    public static final String REQUEST_INVALID = "Request is invalid";
    public static final String CATEGORY = "Category";
    public static final String SUB_CATEGORY = "Sub Category";
    public static final String FINELINE = "Fineline";
    public static final String STYLE = "Style";
    public static final String CUSTOMER_CHOICE = "Customer Choice";
    public static final String MERCH_METHOD = "Merch Method";
    public static final String SIZE = "Size";
    public static final String CHANNEL = "Channel";
    public static final String COLOR_NAME = "Color Name";
    public static final String COLOR_FAMILY = "Color Family";
    public static final Integer DEFAULT_FONT_HEIGHT = 14;
    public static final Integer ZERO = 0;
    public static final String ZERO_STRING = "0";
    public static final String DC_INBOUND_EXCEL_SHEET_NAME = "DCInboundData";
    public static final String DC_INBOUND_HEADER_KEY = "Content-Disposition";
    public static final String DC_INBOUND_REPORT_NAME = "DC_Inbound_Report";
    public static final Integer WP_DEFAULT = 2;
    public static final Integer VP_DEFAULT = 12;
    public static final double VP_WP_RATIO_DEFAULT = 6.0;
    public static final String WM_CONSUMER_ID = "WM_CONSUMER.ID";
    public static final String WM_SVC_NAME = "WM_SVC.NAME";
    public static final String WM_SVC_ENV = "WM_SVC.ENV";
    public static final String NO_ACTION_MSG = "No Action provided";
    public static final String INCORRECT_ACTION_MSG = "Incorrect Action passed";
    public static final String COLOR_COMBINATION_MISSING_MSG = "Color Combinations are missing";
    public static final String COLOR_COMBINATION_EXIST_MSG = "Color Combination already exist";
    public static final String API_TOKEN_KEY ="ApiTokenKey";
    public static  final String MULTI_BUMP_PACK_SUFFIX = "-BP";
    public static final String DEFAULT_FACTORY ="DEFAULT";
    public static final String PACKOPT_FINELINE_DETAILS_SUFFIX = "/api/packOptimization/plan/{planId}/fineline/{finelineNbr}";
    public static final String PACKOPT_FINELINE_STATUS_SUFFIX = "/api/packOptimization/plan/{planId}/fineline/{finelineNbr}/status/{status}";
    public static final String BUMPPACK_DETAILS_SUFFIX = "/bumppack/{bumpPackNbr}";
    public static final Integer DEFAULT_SINGLE_PACK_INDICATOR = 1;
    public static final String PERCENT = "%";
    public static final String BUMP_PACK = "BP";
    public static final String INITIAL_SET = "Initial Set";
    public static final String BUMP_PACK_ERROR = "Bump Pack ";
    public static final String BUMP_PACK_PATTERN = "^(\\d*)_(\\d*)-BP";
    public static final Integer ONLINE_CHANNEL_ID = 2;
    public static final Integer STORE_CHANNEL_ID = 1;
    public static final String DEFAULT_COLOR_FAMILY = "DEFAULT";
    public static final String UNDERSCORE = "_";
    public static final String EMPTY_STRING = "";
    public static final String PACK_OPT_IS_PREFIX = "SP_is";
    public static final String INITIAL_SET_IDENTIFIER = "IS";

    //DC Inbound Report Headers
    public static final List<String> DC_INBOUND_REPORT_DEFAULT_HEADERS = Arrays.asList(CATEGORY, SUB_CATEGORY, FINELINE, STYLE, CUSTOMER_CHOICE, COLOR_NAME, COLOR_FAMILY, MERCH_METHOD, SIZE, CHANNEL);
}
