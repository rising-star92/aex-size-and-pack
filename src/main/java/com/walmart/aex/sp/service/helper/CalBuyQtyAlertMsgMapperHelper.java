package com.walmart.aex.sp.service.helper;

import com.walmart.aex.sp.enums.AppMessageText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Slf4j
public class CalBuyQtyAlertMsgMapperHelper {
    public Set<Integer> getCodesByLevel(Set<Integer> codes, String hierarchyLevel){
        Set<Integer> codesByLevel = new HashSet<>();
        switch (hierarchyLevel) {
            case FINELINE:
                codes.forEach(code -> {
                    if(AppMessageText.SIZE_PROFILE_NOT100_LIST.contains(code)){
                      codesByLevel.add(AppMessageText.SIZE_PROFILE_PCT_NOT100.getId());
                    } else if (AppMessageText.BQFP_ERRORS_LIST.contains(code)) {
                        codesByLevel.add(AppMessageText.BQFP_FL_MESSAGE.getId());
                    } else if (AppMessageText.RFA_ERRORS_LIST.contains(code)) {
                        codesByLevel.add(AppMessageText.RFA_FL_MESSAGE.getId());
                    } else getCommonCodes(codesByLevel, code);
                });
                break;
            case STYLE:
                codes.forEach(code-> {
                    if(AppMessageText.SIZE_PROFILE_NOT100_LIST.contains(code)){
                        codesByLevel.add(AppMessageText.SIZE_PROFILE_PCT_NOT100.getId());
                    } else if(AppMessageText.BQFP_ERRORS_LIST.contains(code)){
                        codesByLevel.add(AppMessageText.BQFP_STYLE_MESSAGE.getId());
                    } else if (AppMessageText.RFA_ERRORS_LIST.contains(code)) {
                        codesByLevel.add(AppMessageText.RFA_STYLE_MESSAGE.getId());
                    } else getCommonCodes(codesByLevel, code);
                });
                break;
            case CUSTOMER_CHOICE:
                codes.forEach(code-> {
                    if(AppMessageText.SIZE_PROFILE_NOT100_LIST.contains(code)){
                        codesByLevel.add(AppMessageText.SIZE_PROFILE_PCT_NOT100_CC_LEVEL.getId());
                    }else{
                        getCommonCodes(codesByLevel, code);
                    }
                });
                break;
            default :
                codesByLevel.addAll(codes);
                break;
        }
        return codesByLevel;
    }

    /***
     * This method returns error codes for One Unit Rule and Admin Rule common for Fineline, Style and CC levels
     * @param codesByLevel
     * @param code
     */
    private void getCommonCodes(Set<Integer> codesByLevel, Integer code) {
        if (AppMessageText.RULE_IS_ONE_UNIT_LIST.contains(code)) {
            codesByLevel.add(AppMessageText.RULE_IS_ONE_UNIT_PER_STORE_APPLIED.getId());
        } else if (AppMessageText.RULE_ADJUST_REPLN_ONE_UNIT_LIST.contains(code)) {
            codesByLevel.add(AppMessageText.RULE_ADJUST_REPLN_FOR_ONE_UNIT_PER_STORE_APPLIED.getId());
        } else if (AppMessageText.RULE_IS_REPLN_THRESHOLD_LIST.contains(code)) {
            codesByLevel.add(AppMessageText.RULE_IS_REPLN_ITM_PC_APPLIED.getId());
        } else if (AppMessageText.RULE_ADJUST_REPLN_THRESHOLD_LIST.contains(code)) {
            codesByLevel.add(AppMessageText.RULE_ADJUST_MIN_REPLN_THRESHOLD_APPLIED.getId());
        } else {
            codesByLevel.add(code);
        }
    }
}
