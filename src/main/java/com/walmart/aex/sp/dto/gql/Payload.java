package com.walmart.aex.sp.dto.gql;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import lombok.Data;

@Data
public class Payload {
    private BuyQtyResponse getCcSizeClus;
    private APResponse getRFADataFromSizePack;
    private BuyQtyResponse getAllCcSizeClus;
    private BuyQtyResponse getFinelinesWithSizes;
    private BuyQtyResponse getStylesCcWithSizes;
}
