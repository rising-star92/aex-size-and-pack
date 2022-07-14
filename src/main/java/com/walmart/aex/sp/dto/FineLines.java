package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class FineLines {
    private Integer fineLineNbr;
    private String fineLineDesc;
    private MetricsDto metrics;
    private List<ClustersDto> cluster;
}
