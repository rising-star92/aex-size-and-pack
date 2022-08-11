package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

import java.util.List;

@Data
public class StoreQuantity {
    private Integer isUnits;
    private Integer totalUnits;
    private List<Integer> storeList;
    private Integer sizeCluster;
    private Integer volumeCluster;
    private List<BumpSetQuantity> bumpSets;
}
