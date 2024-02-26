package com.walmart.aex.sp.dto.buyquantity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyQtyQueryRequest {
    private Integer planId;
    private Integer lvl0;
    private Integer lvl1;
    private Integer lvl2;
    private Integer lvl3;
    private Integer lvl4;
    private Integer finelineNbr;
    private Integer likeFinelineNbr;
    private Integer likeLvl1;
    private Integer likeLvl3;
    private Integer likeLvl4;
    private String colors;
    private Integer fiscalYear;
    private String seasonCode;
    private String volDeviation;
}