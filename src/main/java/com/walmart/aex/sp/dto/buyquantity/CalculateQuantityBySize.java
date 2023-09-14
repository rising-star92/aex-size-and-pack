package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
public class CalculateQuantityBySize extends CalculateQuantity {
    private String sizeDesc;
    private InitialSetQuantity initialSetQuantity;
    private List<BumpSetQuantity> bumpSetQuantities;
}
