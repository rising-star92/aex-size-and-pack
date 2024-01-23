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
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ValidationServiceImpl implements ValidationService {

    private final RFAValidationService rfaValidationService;
    @Autowired
    BQFPValidationsService bqfpValidationsService;

    public ValidationServiceImpl(RFAValidationService rfaValidationService) {
        this.rfaValidationService = rfaValidationService;
    }

    /**
     * All input validations related to calculate buy quantity
     */
    @Override
    public ValidationResult validateCalculateBuyQuantityInputData(List<MerchMethodsDto> merchMethodsDtos, APResponse apResponse, BQFPResponse bqfpResponse, StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        List<Integer> allValidationCodes = new ArrayList<>();
        // separate method to call each individual validation service
        ValidationResult bqfpValidationResult = bqfpValidationsService.missingBuyQuantity(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);
        ValidationResult rfaValidationResult = rfaValidationService.validateRFAData(apResponse, bqfpResponse, styleDto.getStyleNbr(), customerChoiceDto);
        allValidationCodes.addAll(bqfpValidationResult.getCodes());
        allValidationCodes.addAll(rfaValidationResult.getCodes());

        return ValidationResult.builder().codes(allValidationCodes).build();
    }
}
