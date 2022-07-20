package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomerChoiceList {

    private String ccId;
    private String colorName;
    private MetricsDto metrics;
    private List<ClustersDto> clusters;


}
