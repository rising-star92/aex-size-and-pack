package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationCode;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.service.ValidationService;

import java.util.Collections;
import java.util.List;

public class ValidationServiceImpl implements ValidationService {

    //All validation service per dataset - RFAValidationService, BQFPValidationService

    @Override
    public ValidationCode validateCalculateBuyQuantityData(List<MerchMethodsDto> merchMethodsDtos, APResponse apResponse, BQFPResponse bqfpResponse, Integer styleNbr, CustomerChoiceDto customerChoiceDto) {
        // separate method to call each individual validation service
        return null;
    }

}
