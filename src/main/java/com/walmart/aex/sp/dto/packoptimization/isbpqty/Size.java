package com.walmart.aex.sp.dto.packoptimization.isbpqty;

import lombok.Data;

@Data
public class Size {
    private String sizeDesc;
    private Integer optFinalBuyQty;
    private Integer optFinalInitialSetQty;
    private Integer optFinalBumpSetQty;
}
