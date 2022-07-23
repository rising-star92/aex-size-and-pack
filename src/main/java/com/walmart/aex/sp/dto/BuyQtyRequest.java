package com.walmart.aex.sp.dto;

import lombok.Data;

@Data
public class BuyQtyRequest {
    private Long planId;
    private String channel;
    private String planDesc;
}
