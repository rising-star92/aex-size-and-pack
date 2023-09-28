package com.walmart.aex.sp.dto.buyquantity;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class CalculateInitialSet {
    private Long totalUnits;
    private List<InitialSetQuantity> initialSetQuantities;
}