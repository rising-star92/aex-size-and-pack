package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data	
@NoArgsConstructor
public class FineLinePackOptimizationResponseDTO {
	private Long planId;
	private String planDesc;
	private Integer finelineNbr;
	private String ccId;
	private String fixtureTypeRollupName;
	private String merchMethod;
	private String fpStrategyText;
	private String ahsSizeDesc;
	private String storeObj;

public FineLinePackOptimizationResponseDTO(Long planId,//String planDesc,
			Integer finelineNbr, String ccId,  String  fixtureTypeRollupName,
			String merchMethod,String fpStrategyText,String ahsSizeDesc,String storeObj ) {
		this.planId = planId;
		//this.planDesc=planDesc;
		this.finelineNbr = finelineNbr;
		this.ccId = ccId;
		this.fixtureTypeRollupName=fixtureTypeRollupName;
		this.merchMethod = merchMethod;
		this.fpStrategyText=fpStrategyText;
		this.ahsSizeDesc=ahsSizeDesc;
		this.storeObj=storeObj;


	}


}
