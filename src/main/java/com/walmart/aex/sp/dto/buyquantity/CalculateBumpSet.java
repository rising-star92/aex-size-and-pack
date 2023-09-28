package com.walmart.aex.sp.dto.buyquantity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CalculateBumpSet {
    private Long totalUnits;
    private List<BumpSetQuantity> bumpSetQuantities;
}