package com.walmart.aex.sp.dto.buyQuantity;

import com.walmart.aex.sp.dto.buyQuantity.MetricsDto;
import lombok.Data;

@Data
public class SizeDto {
    private Integer sizeId;
    private String sizeDesc;
    private MetricsDto metrics;
}
