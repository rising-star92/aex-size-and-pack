package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;

import java.util.List;

public interface RFAValidationService {
    ValidationResult validateRFAData(List<MerchMethodsDto> merchMethodsDtoList, APResponse apResponse, String styleNbr, CustomerChoiceDto customerChoiceDto);
}
