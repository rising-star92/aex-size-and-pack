package com.walmart.aex.sp.dto.buyQuantity;


import com.walmart.aex.sp.dto.buyQuantity.ClustersDto;
import com.walmart.aex.sp.dto.buyQuantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyQuantity.MetricsDto;
import lombok.Data;

import java.util.List;

@Data
public class StyleDto {
    private String styleNbr;
    private MetricsDto metrics;
    private List<ClustersDto> clusters;
    private List<CustomerChoiceDto> customerChoices;
}
