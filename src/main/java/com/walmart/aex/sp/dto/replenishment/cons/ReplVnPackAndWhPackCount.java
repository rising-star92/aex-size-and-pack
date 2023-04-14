package com.walmart.aex.sp.dto.replenishment.cons;

import lombok.Data;

@Data
public class ReplVnPackAndWhPackCount {
    private Integer vendorPackCount;
    private Integer warehousePackCount;
    private Double vendorPackWarHousePackRatio;
}
