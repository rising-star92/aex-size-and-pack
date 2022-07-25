package com.walmart.aex.sp.dto.buyquantity;


import lombok.Data;

import java.util.List;

@Data
public class StyleDto {
    private String styleNbr;
    private MetricsDto metrics;
    private List<ClustersDto> clusters;
    private List<CustomerChoiceDto> customerChoices;
}
