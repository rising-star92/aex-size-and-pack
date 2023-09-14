package com.walmart.aex.sp.dto.buyquantity;

import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import lombok.Data;

@Data
public class InitialSetQuantity {
    private double isQty;
    private long calculatedISQty;
    private double perStoreQty;
    private long roundedPerStoreQty;
    private double originalPerStoreQty;
    private boolean oneUnitPerStore;
    private String sizeDesc;
    private double sizePct;
    private RFASizePackData rfaSizePackData;
    private Cluster volumeCluster;
}
