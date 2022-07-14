package com.walmart.aex.sp.dto;

import lombok.Data;

@Data
public class SizeList {
    private Integer sizeId;
    private String sizeDesc;
    private MetricsDto metrics;
}
