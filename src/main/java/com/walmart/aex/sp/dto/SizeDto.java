package com.walmart.aex.sp.dto;

import lombok.Data;

@Data
public class SizeDto {
    private Integer sizeId;
    private String sizeDesc;
    private MetricsDto metrics;
}
