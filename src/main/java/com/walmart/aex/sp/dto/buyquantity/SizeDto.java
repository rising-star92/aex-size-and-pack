package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class SizeDto {
    private Integer ahsSizeId;
    private Integer sizeId;
    private String sizeDesc;
    private MetricsDto metrics;
}
