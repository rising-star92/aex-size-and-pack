package com.walmart.aex.sp.dto.buyquantity;

import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import lombok.Data;

import java.util.List;

@Data
public class AddStoreBuyQuantity {
    private StyleDto styleDto;
    private CustomerChoiceDto customerChoiceDto;
    private MerchMethodsDto merchMethodsDto;
    private BQFPResponse bqfpResponse;
    private SizeDto sizeDto;
    private List<RFASizePackData> rfaSizePackDataList;
}
