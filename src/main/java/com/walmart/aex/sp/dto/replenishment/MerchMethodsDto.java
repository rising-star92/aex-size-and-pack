package com.walmart.aex.sp.dto.replenishment;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import lombok.Data;

import java.util.List;

@Data
public class MerchMethodsDto {
    private String merchMethod;
    private MetricsDto metrics;
    private List<SizeDto> sizes;

}
