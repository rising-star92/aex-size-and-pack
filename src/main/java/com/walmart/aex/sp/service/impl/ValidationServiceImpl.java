package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.service.BQFPValidationsService;
import com.walmart.aex.sp.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationServiceImpl implements ValidationService {

    //All validation service per dataset - RFAValidationService, BQFPValidationService
    @Autowired
    BQFPValidationsService bqfpValidationsService;

    @Override
    public ValidationResult validateCalculateBuyQuantityInputData(List<MerchMethodsDto> merchMethodsDtos, APResponse apResponse, BQFPResponse bqfpResponse,
                                                                  StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        List<Integer> allValidationCodes = new ArrayList<>();
        // separate method to call each individual validation service
        ValidationResult bqfpValidationResult = bqfpValidationsService.missingBuyQuantity(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);
        allValidationCodes.addAll(bqfpValidationResult.getCodes());

        return ValidationResult.builder().codes(allValidationCodes).build();
    }
}
