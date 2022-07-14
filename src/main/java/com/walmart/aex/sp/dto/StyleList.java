package com.walmart.aex.sp.dto;


import lombok.Data;

import java.util.List;

@Data
public class StyleList {

    private Integer styleNbr;
    private MetricsDto metricsObj;
    private List<ClustersDto> clusterList;


}
