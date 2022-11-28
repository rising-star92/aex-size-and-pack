package com.walmart.aex.sp.dto.buyquantity;

import com.walmart.aex.sp.dto.bqfp.Replenishment;
import lombok.Data;

import java.util.List;

@Data
public class InitialSetWithReplnsConstraint extends InitialSetQuantity {
    private List<Replenishment> replnsWithUnits;
    private StoreQuantity storeQuantity;
}
