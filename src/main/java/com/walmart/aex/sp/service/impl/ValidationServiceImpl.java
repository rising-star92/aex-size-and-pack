package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.service.RFAValidationService;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.service.BQFPValidationsService;
import com.walmart.aex.sp.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ValidationServiceImpl implements ValidationService {

    private final RFAValidationService rfaValidationService;
    private final BQFPValidationsService bqfpValidationsService;

    public ValidationServiceImpl(RFAValidationService rfaValidationService, BQFPValidationsService bqfpValidationsService) {
        this.rfaValidationService = rfaValidationService;
        this.bqfpValidationsService = bqfpValidationsService;
    }

    /**
     * All input validations related to calculate buy quantity
     */
    @Override
    public ValidationResult validateCalculateBuyQuantityInputData(List<MerchMethodsDto> merchMethodsDtos, APResponse apResponse, BQFPResponse bqfpResponse, StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        ValidationResult bqfpValidationResult = bqfpValidationsService.missingBuyQuantity(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);
        ValidationResult rfaValidationResult = ValidationResult.builder().codes(new HashSet<>()).build();
        if (ObjectUtils.allNotNull(apResponse, apResponse.getRfaSizePackData()))
            rfaValidationResult = rfaValidationService.validateRFAData(merchMethodsDtos, apResponse, styleDto.getStyleNbr(), customerChoiceDto);
        Set<Integer> allValidationCodes = Stream.of(bqfpValidationResult.getCodes(), rfaValidationResult.getCodes())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return ValidationResult.builder().codes(allValidationCodes).build();
    }
}
