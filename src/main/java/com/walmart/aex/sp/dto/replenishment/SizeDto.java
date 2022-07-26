package com.walmart.aex.sp.dto.replenishment;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import lombok.Data;

@Data
public class SizeDto {
    private Integer ahsSizeId;
    private String sizeDesc;
    private MetricsDto metrics;
}
