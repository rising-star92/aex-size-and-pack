package com.walmart.aex.sp.dto.buyquantity;

import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import lombok.Data;

import java.util.List;

@Data
public class FinelineDto {
	 private Integer finelineNbr;
	    private String finelineDesc;
	    private String finelineAltDesc;
	    private MetricsDto metrics;
	    private List<ClustersDto> clusters;
	    private List<StyleDto> styles;
	    private List<MerchMethodsDto> merchMethods;
	  
}
