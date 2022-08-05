package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

@Data
public class BumpSetQuantity {
    private Integer setNbr;
    private String wmYearWeek;
    private String weekDesc;
    private Integer bsUnits;
    private Integer totalUnits;
}
