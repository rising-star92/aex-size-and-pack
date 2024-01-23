package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationCode;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.service.BQFPValidationsService;
import com.walmart.aex.sp.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationServiceImpl implements ValidationService {

    //All validation service per dataset - RFAValidationService, BQFPValidationService
    @Autowired
    BQFPValidationsService bqfpValidationsService;

    @Override
    public List<Integer> validateCalculateBuyQuantityInputData(List<MerchMethodsDto> merchMethodsDtos, APResponse apResponse, BQFPResponse bqfpResponse, String styleNbr,
                                                               StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        // separate method to call each individual validation service
        List<Integer> bqfpValidationCodes = bqfpValidationsService.missingBuyQuantity(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);
        return new ArrayList<>(bqfpValidationCodes);
    }

}
