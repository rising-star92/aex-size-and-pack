package com.walmart.aex.sp.dto.buyquantity;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import lombok.Data;

@Data
public class Payload {
    private BuyQtyResponse getCcSizeClus;
    private APResponse getRFADataFromSizePack;
}
