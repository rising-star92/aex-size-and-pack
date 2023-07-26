package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

@Data
public class InitialSetWithReplnsConstraint extends InitialSetQuantity {
    private StoreQuantity storeQuantity;
}
