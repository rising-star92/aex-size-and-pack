package com.walmart.aex.sp.dto.buyquantity;
import lombok.Data;

import java.util.List;

@Data
public class MerchMethodsDto {
    private String fixtureType;
    private Integer fixtureTypeRollupId;
    private String merchMethod;
    private MetricsDto metrics;
    private List<SizeDto> sizes;
    private List<ClustersDto> clusters;
}
