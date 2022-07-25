package com.walmart.aex.sp.dto.buyQuantity;

import lombok.Data;

@Data
public class BuyQtyRequest {
    private Long planId;
    private String channel;
    private String planDesc;
}
