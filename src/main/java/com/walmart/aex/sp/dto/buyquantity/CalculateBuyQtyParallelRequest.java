package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CalculateBuyQtyParallelRequest {
    private Long planId;
    private String channel;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
}
