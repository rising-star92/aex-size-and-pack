package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class CalculateQuantityByUnit extends CalculateQuantity {
    private Long totalUnitsFromBQ;
    private Integer storeCount;
    private CalculateInitialSet calculateInitialSet;
    private CalculateBumpSet calculateBumpSet;
}
