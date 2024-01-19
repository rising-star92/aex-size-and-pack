package com.walmart.aex.sp.dto.buyquantity;


import lombok.Data;

import java.util.List;

@Data
public class StyleDto {
    private String styleNbr;
    private String altStyleDesc;
    private Metadata metadata;
    private MetricsDto metrics;
    private List<ClustersDto> clusters;
    private List<CustomerChoiceDto> customerChoices;
    private Integer channelId;
}
