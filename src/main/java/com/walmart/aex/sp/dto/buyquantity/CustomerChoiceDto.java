package com.walmart.aex.sp.dto.buyquantity;

import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import lombok.Data;

import java.util.List;

@Data
public class CustomerChoiceDto {

    private String ccId;
    private String colorName;
    private String colorFamilyDesc;
    private MetricsDto metrics;
    private List<ClustersDto> clusters;
    private List<MerchMethodsDto> merchMethods;
    private Integer channelId;
}
