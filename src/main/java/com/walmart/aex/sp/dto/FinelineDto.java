package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class FinelineDto {
    private Integer finelineNbr;
    private String finelineDesc;
    private MetricsDto metrics;
    private List<ClustersDto> clusters;
    private List<StyleDto> styles;
}
