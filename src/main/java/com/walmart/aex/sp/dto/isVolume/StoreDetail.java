package com.walmart.aex.sp.dto.isVolume;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreDetail {
    private Integer store;
    private String groupingType;
    private Integer qty;
}
