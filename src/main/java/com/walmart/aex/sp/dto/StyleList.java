package com.walmart.aex.sp.dto;


import lombok.Data;

import java.util.List;

@Data
public class StyleList {

    private String styleNbr;
    private MetricsDto metrics;
    private List<ClustersDto> cluster;


}
