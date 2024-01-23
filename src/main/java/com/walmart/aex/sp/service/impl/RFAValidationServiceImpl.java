package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.CustomerChoice;
import com.walmart.aex.sp.dto.bqfp.Fixture;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.enums.AppMessageText;
import com.walmart.aex.sp.service.RFAValidationService;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.SizeAndPackConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RFAValidationServiceImpl implements RFAValidationService {

    @Override
    public ValidationResult validateRFAData(APResponse apResponse, BQFPResponse bqfpResponse, String styleNbr, CustomerChoiceDto customerChoiceDto) {
        List<Integer> rfaValidationCodes = new ArrayList<>();
        if (apResponse.getRfaSizePackData().isEmpty()) {
            // if rfa is empty
            rfaValidationCodes.add(AppMessageText.RFA_NOT_AVAILABLE.getId());
            return ValidationResult.builder().messages(rfaValidationCodes).build();
        }
        List<RFASizePackData> rfaSizePackDataList = apResponse.getRfaSizePackData().stream().filter(rfa -> rfa.getCustomer_choice().equalsIgnoreCase(customerChoiceDto.getCcId())).collect(Collectors.toList());
        if (rfaSizePackDataList.isEmpty()) {
            // rfa is missing for CC
            rfaValidationCodes.add(AppMessageText.RFA_CC_NOT_AVAILABLE.getId());
            return ValidationResult.builder().messages(rfaValidationCodes).build();
        }
        CustomerChoice ccFromBQFP = BuyQtyCommonUtil.getCcFromBQFP(styleNbr, customerChoiceDto.getCcId(), bqfpResponse);
        validateFixture(ccFromBQFP, rfaSizePackDataList, rfaValidationCodes);
        validateColorFamily(customerChoiceDto, rfaSizePackDataList, rfaValidationCodes);
        return ValidationResult.builder().messages(rfaValidationCodes).build();
    }

    /**
     * RFA missing any fixture type
     */
    private void validateFixture(CustomerChoice ccFromBQFP, List<RFASizePackData> rfaSizePackDataList, List<Integer> rfaValidationCodes) {
        for (Fixture fixture : ccFromBQFP.getFixtures()) {
            if (rfaSizePackDataList.stream().noneMatch(rfa -> rfa.getFixture_type().equalsIgnoreCase(fixture.getFixtureType())))
                rfaValidationCodes.add(AppMessageText.RFA_MISSING_FIXTURE.getId());
        }
    }

    /**
     * RFA is missing any color families
     */
    private void validateColorFamily(CustomerChoiceDto customerChoiceDto, List<RFASizePackData> rfaSizePackDataList, List<Integer> rfaValidationCodes) {
        RFASizePackData rfaSizePackData = rfaSizePackDataList.stream().filter(rfa -> rfa.getCustomer_choice().equalsIgnoreCase(customerChoiceDto.getCcId())).findFirst().orElse(null);
        if (null != rfaSizePackData && StringUtils.isNotEmpty(rfaSizePackData.getColor_family()) &&
                !((StringUtils.isNotEmpty(customerChoiceDto.getColorFamily()) && rfaSizePackData.getColor_family().equalsIgnoreCase(customerChoiceDto.getColorFamily())) ||
                rfaSizePackData.getColor_family().equalsIgnoreCase(SizeAndPackConstants.DEFAULT_COLOR_FAMILY)))
            rfaValidationCodes.add(AppMessageText.RFA_MISSING_COLOR_FAMILY.getId());
    }
}
