package com.walmart.aex.sp.dto.buyquantity;

import java.util.List;

import com.walmart.aex.sp.dto.packoptimization.FixtureDto;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;

import lombok.Data;

@Data
public class CustomerChoiceDto {

	private String ccId;
    private String colorName;
    private MetricsDto metrics;
    private List<ClustersDto> clusters;
    private List<MerchMethodsDto> merchMethods;
    private List<FixtureDto> fixtures;


}
