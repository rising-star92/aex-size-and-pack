package com.walmart.aex.sp.dto.bqfp;

import com.walmart.aex.sp.dto.buyquantity.StoreQuantity;
import lombok.Data;

import java.util.List;

@Data
public class InitialSetWithReplnsConstraint {
    private double isQty;
    private double perStoreQty;
    private List<Replenishment> replnsWithUnits;
    private StoreQuantity storeQuantity;
}
