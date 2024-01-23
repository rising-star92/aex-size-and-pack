package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;

public interface RFAValidationService {
    ValidationResult validateRFAData(APResponse apResponse, BQFPResponse bqfpResponse, String styleNbr, CustomerChoiceDto customerChoiceDto);
}
