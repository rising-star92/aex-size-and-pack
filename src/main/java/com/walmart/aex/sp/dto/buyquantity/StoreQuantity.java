package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

import java.util.List;

@Data
public class StoreQuantity {
    private Double isUnits;
    private Double totalUnits;
    private List<Integer> storeList;
    private Integer sizeCluster;
    private Integer volumeCluster;
    private List<BumpSetQuantity> bumpSets;
    private Integer flowStrategyCode;
}
