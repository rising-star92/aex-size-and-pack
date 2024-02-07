package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.appmessage.AppMessageTextRequest;
import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;
import com.walmart.aex.sp.dto.mapper.AppMessageTextMapper;
import com.walmart.aex.sp.entity.AppMessageText;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.AppMessageTextRepository;
import com.walmart.aex.sp.service.AppMessageTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Slf4j
public class AppMessageTextServiceImpl implements AppMessageTextService {

    private final AppMessageTextRepository appMessageTextRepository;

    public AppMessageTextServiceImpl(AppMessageTextRepository appMessageTextRepository) {
        this.appMessageTextRepository = appMessageTextRepository;
    }

    @Override
    @Cacheable("appMessages")
    public List<AppMessageTextResponse> getAllAppMessageText() {
        try {
            List<AppMessageText> appMessageTexts = appMessageTextRepository.findAll();
            return AppMessageTextMapper.mapper.mapEntityToResponse(appMessageTexts);
        } catch (Exception ex) {
            log.error("Error while getting list of App messages. Exception: {}", ex.getMessage());
            throw new CustomException("Exception occurred while retrieving app messages");
        }

    }

    @Override
    @CacheEvict(value = "appMessages", allEntries = true)
    public void addAppMessageTexts(List<AppMessageTextRequest> appMessageTextRequests) {
        try {
            if (!CollectionUtils.isEmpty(appMessageTextRequests)) {
                List<AppMessageText> appMessageTexts = AppMessageTextMapper.mapper.mapRequestToEntity(appMessageTextRequests);
                appMessageTextRepository.saveAll(appMessageTexts);
            }
        } catch (Exception ex) {
            log.error("Failed to add new app message. Exception: {}", ex.getMessage());
            throw new CustomException("Failed to add app message");
        }
    }

    @Override
    @CacheEvict(value = "appMessages", allEntries = true)
    public void updateAppMessageTexts(List<AppMessageTextRequest> appMessageTextRequests) {
        try {
            List<AppMessageText> updatedRecords = new ArrayList<>();
            if (!CollectionUtils.isEmpty(appMessageTextRequests)) {
                List<AppMessageText> appMessageTexts = AppMessageTextMapper.mapper.mapRequestToEntity(appMessageTextRequests);
                List<AppMessageText> appMessageTextsFromDb = appMessageTextRepository.findAllById(appMessageTexts.stream().map(AppMessageText::getId).collect(Collectors.toList()));
                for (AppMessageText appMessageText : appMessageTexts) {
                    if (appMessageTextsFromDb.stream().anyMatch(app -> app.getId().equals(appMessageText.getId())))
                        updatedRecords.add(appMessageText);
                }
                if (!CollectionUtils.isEmpty(updatedRecords))
                    appMessageTextRepository.saveAll(updatedRecords);
            }
        } catch (Exception ex) {
            log.error("Failed to update app messages. Exception: {}", ex.getMessage());
            throw new CustomException("Failed to update app messages.");
        }
    }

    @Override
    @CacheEvict(value = "appMessages", allEntries = true)
    public void deleteAppMessageTexts(List<Integer> appMessageCodesRequest) {
        try {
            if (!CollectionUtils.isEmpty(appMessageCodesRequest)) {
                appMessageTextRepository.deleteAllById(appMessageCodesRequest);
            }
        } catch (Exception ex) {
            log.error("Failed to delete app messages. Exception: {}", ex.getMessage());
            throw new CustomException("Failed to delete app messages.");
        }
    }

    public List<AppMessageTextResponse> getAppMessagesByIds(Set<Integer> validationCodes) {
        try {
            List<AppMessageTextResponse> appMessageTextResponseList = getAllAppMessageText();
            return appMessageTextResponseList.stream()
                    .filter(appMessageTextResponse -> validationCodes.contains(appMessageTextResponse.getId()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error while getting list of App messages by error codes. Exception: {}", ex.getMessage());
            throw new CustomException("Exception occurred while retrieving app messages");
        }

    }

    /***
     * This method will return List of AppMessageTextResponse based on hierarchy level by taking in the alert codes
     * @param codes
     * @param hierarchyLevel
     * @return AppMessageTextResponse list
     */
    public List<AppMessageTextResponse> getMatchingAppMessageTexts(Set<Integer> codes, String hierarchyLevel) {
        Set<Integer> codesByLevel = getCodesByLevel(codes,hierarchyLevel);
        return getAppMessagesByIds(codesByLevel);
    }

    /***
     * This method will return the alert error codes by the hierarchy type
     * @param codes
     * @param hierarchyLevel
     * @return
     */
    public Set<Integer> getCodesByLevel(Set<Integer> codes, String hierarchyLevel){
        Set<Integer> codesByLevel = new HashSet<>();
        switch (hierarchyLevel) {
            case FINELINE:
                codes.forEach(code -> {
                    if(com.walmart.aex.sp.enums.AppMessageText.SIZE_PROFILE_PCT_NOT100_CC_LEVEL.getId().equals(code)){
                        codesByLevel.add(com.walmart.aex.sp.enums.AppMessageText.SIZE_PROFILE_PCT_NOT100.getId());
                    } else if (com.walmart.aex.sp.enums.AppMessageText.BQFP_ERRORS_LIST.contains(code)) {
                        codesByLevel.add(com.walmart.aex.sp.enums.AppMessageText.BQFP_FL_MESSAGE.getId());
                    } else if (com.walmart.aex.sp.enums.AppMessageText.RFA_ERRORS_LIST.contains(code)) {
                        codesByLevel.add(com.walmart.aex.sp.enums.AppMessageText.RFA_FL_MESSAGE.getId());
                    } else getSizeAlertCodesForOtherLevels(codesByLevel, code);
                });
                break;
            case STYLE:
                codes.forEach(code-> {
                    if(com.walmart.aex.sp.enums.AppMessageText.SIZE_PROFILE_PCT_NOT100_CC_LEVEL.getId().equals(code)){
                        codesByLevel.add(com.walmart.aex.sp.enums.AppMessageText.SIZE_PROFILE_PCT_NOT100.getId());
                    } else if(com.walmart.aex.sp.enums.AppMessageText.BQFP_ERRORS_LIST.contains(code)){
                        codesByLevel.add(com.walmart.aex.sp.enums.AppMessageText.BQFP_STYLE_MESSAGE.getId());
                    } else if (com.walmart.aex.sp.enums.AppMessageText.RFA_ERRORS_LIST.contains(code)) {
                        codesByLevel.add(com.walmart.aex.sp.enums.AppMessageText.RFA_STYLE_MESSAGE.getId());
                    } else getSizeAlertCodesForOtherLevels(codesByLevel, code);
                });
                break;
            case CUSTOMER_CHOICE:
                codes.forEach(code-> getSizeAlertCodesForOtherLevels(codesByLevel, code));
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
    private void getSizeAlertCodesForOtherLevels(Set<Integer> codesByLevel, Integer code) {
        if (com.walmart.aex.sp.enums.AppMessageText.RULE_INITIALSET_ONE_UNIT_PER_STORE_APPLIED.getId().equals(code)) {
            codesByLevel.add(com.walmart.aex.sp.enums.AppMessageText.RULE_IS_ONE_UNIT_PER_STORE_APPLIED.getId());
        } else if (com.walmart.aex.sp.enums.AppMessageText.RULE_ADJUST_REPLN_FOR_ONE_UNIT_PER_STORE_APPLIED.getId().equals(code)) {
            codesByLevel.add(com.walmart.aex.sp.enums.AppMessageText.RULE_ADJUST_REPLN_ONE_UNIT_PER_STORE_APPLIED.getId());
        } else if (com.walmart.aex.sp.enums.AppMessageText.RULE_MIN_INITIALSET_THRESHOLD_APPLIED.getId().equals(code)) {
            codesByLevel.add(com.walmart.aex.sp.enums.AppMessageText.RULE_IS_REPLN_ITM_PC_APPLIED.getId());
        } else if (com.walmart.aex.sp.enums.AppMessageText.RULE_REPLN_UNITS_MOVED_TO_INITIAL_SET_APPLIED.getId().equals(code)) {
            codesByLevel.add(com.walmart.aex.sp.enums.AppMessageText.RULE_ADJUST_MIN_REPLN_THRESHOLD_APPLIED.getId());
        } else {
            codesByLevel.add(code);
        }
    }
}
