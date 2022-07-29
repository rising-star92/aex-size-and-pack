package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

@Data
public class BuyQtyRequest {
    private Long planId;
    private String channel;
    private String planDesc;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private String styleNbr;
    private String ccId;
}
