package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.enums.AppMessageText;
import com.walmart.aex.sp.service.RFAValidationService;
import com.walmart.aex.sp.util.SizeAndPackConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RFAValidationServiceImpl implements RFAValidationService {

    @Override
    public ValidationResult validateRFAData(List<MerchMethodsDto> merchMethodsDtoList, APResponse apResponse, String styleNbr, CustomerChoiceDto customerChoiceDto) {
        Set<Integer> rfaValidationCodes = new HashSet<>();
        if (apResponse.getRfaSizePackData().isEmpty()) {
            // if rfa is empty
            rfaValidationCodes.add(AppMessageText.RFA_NOT_AVAILABLE.getId());
            return buildResult(rfaValidationCodes);
        }
        List<RFASizePackData> rfaSizePackDataList = apResponse.getRfaSizePackData().stream().filter(rfa -> rfa.getCustomer_choice().equalsIgnoreCase(customerChoiceDto.getCcId())).collect(Collectors.toList());
        if (rfaSizePackDataList.isEmpty()) {
            // rfa is missing for CC
            rfaValidationCodes.add(AppMessageText.RFA_CC_NOT_AVAILABLE.getId());
            return buildResult(rfaValidationCodes);
        }
        List<String> fixtureTypes = merchMethodsDtoList.stream().map(MerchMethodsDto::getFixtureType).collect(Collectors.toList());
        validateFixture(fixtureTypes, rfaSizePackDataList, rfaValidationCodes);
        validateColorFamily(customerChoiceDto, rfaSizePackDataList, rfaValidationCodes);
        return buildResult(rfaValidationCodes);
    }

    private ValidationResult buildResult(Set<Integer> rfaValidationCodes) {
        return ValidationResult.builder().codes(rfaValidationCodes).build();
    }

    /**
     * RFA missing any fixture type
     */
    private void validateFixture(List<String> fixtureTypes, List<RFASizePackData> rfaSizePackDataList, Set<Integer> rfaValidationCodes) {
        for (String fixtureType : fixtureTypes) {
            if (rfaSizePackDataList.stream().noneMatch(rfa -> rfa.getFixture_type().equalsIgnoreCase(fixtureType)))
                rfaValidationCodes.add(AppMessageText.RFA_MISSING_FIXTURE.getId());
        }
    }

    /**
     * RFA is missing any color families
     */
    private void validateColorFamily(CustomerChoiceDto customerChoiceDto, List<RFASizePackData> rfaSizePackDataList, Set<Integer> rfaValidationCodes) {
        RFASizePackData rfaSizePackData = rfaSizePackDataList.stream().filter(rfa -> rfa.getCustomer_choice().equalsIgnoreCase(customerChoiceDto.getCcId())).findFirst().orElse(null);
        if (null != rfaSizePackData && StringUtils.isNotEmpty(rfaSizePackData.getColor_family()) &&
                !((StringUtils.isNotEmpty(customerChoiceDto.getColorFamily()) && rfaSizePackData.getColor_family().equalsIgnoreCase(customerChoiceDto.getColorFamily())) ||
                rfaSizePackData.getColor_family().equalsIgnoreCase(SizeAndPackConstants.DEFAULT_COLOR_FAMILY)))
            rfaValidationCodes.add(AppMessageText.RFA_MISSING_COLOR_FAMILY.getId());
    }
}
