package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.enums.AppMessage;
import com.walmart.aex.sp.service.RFAValidationService;
import com.walmart.aex.sp.util.SizeAndPackConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class RFAValidationServiceImpl implements RFAValidationService {

    @Override
    public ValidationResult validateRFAData(List<MerchMethodsDto> merchMethodsDtoList, APResponse apResponse, String styleNbr, CustomerChoiceDto customerChoiceDto) {
        List<RFASizePackData> rfaSizePackDataList = apResponse.getRfaSizePackData().stream().filter(rfa -> rfa.getCustomer_choice().equalsIgnoreCase(customerChoiceDto.getCcId())).collect(Collectors.toList());
        Integer colorFamilyCode = validateColorFamily(customerChoiceDto, rfaSizePackDataList);
        Set<Integer> rfaValidationCodes = Stream.of(colorFamilyCode).filter(Objects::nonNull).collect(Collectors.toSet());
        return buildResult(rfaValidationCodes);
    }

    private ValidationResult buildResult(Set<Integer> rfaValidationCodes) {
        return ValidationResult.builder().codes(rfaValidationCodes).build();
    }

    /**
     * RFA is missing any color families
     */
    private Integer validateColorFamily(CustomerChoiceDto customerChoiceDto, List<RFASizePackData> rfaSizePackDataList) {
        Integer colorFamilyCode = null;
        RFASizePackData rfaSizePackData = rfaSizePackDataList.stream().filter(rfa -> rfa.getCustomer_choice().equalsIgnoreCase(customerChoiceDto.getCcId())).findFirst().orElse(null);
        if (null != rfaSizePackData && StringUtils.isNotEmpty(rfaSizePackData.getColor_family()) &&
                !((StringUtils.isNotEmpty(customerChoiceDto.getColorFamily()) && rfaSizePackData.getColor_family().equalsIgnoreCase(customerChoiceDto.getColorFamily())) ||
                rfaSizePackData.getColor_family().equalsIgnoreCase(SizeAndPackConstants.DEFAULT_COLOR_FAMILY)))
            colorFamilyCode = AppMessage.RFA_MISSING_COLOR_FAMILY.getId();
        return colorFamilyCode;
    }
}
