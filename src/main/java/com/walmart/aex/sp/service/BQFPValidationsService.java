package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;

import java.util.List;

public interface BQFPValidationsService {
    ValidationResult missingBuyQuantity(List<MerchMethodsDto> merchMethodsDtos, BQFPResponse bqfpResponse,
                                        StyleDto styleDto, CustomerChoiceDto customerChoiceDto);
}
