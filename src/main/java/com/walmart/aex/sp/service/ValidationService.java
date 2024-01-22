package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationCode;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;

import java.util.List;

public interface ValidationService {
    public ValidationCode validateCalculateBuyQuantityData(List<MerchMethodsDto> merchMethodsDtos, APResponse apResponse, BQFPResponse bqfpResponse, String styleNbr, CustomerChoiceDto customerChoiceDto);
}
