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
	private Integer merchMethod;
	private String ahsSizeDesc;
	private String storeObj;

public FineLinePackOptimizationResponseDTO(Long planId,
			Integer finelineNbr, String ccId,  String  fixtureTypeRollupName,
										   Integer merchMethod,String ahsSizeDesc,String storeObj ) {
		this.planId = planId;
		this.finelineNbr = finelineNbr;
		this.ccId = ccId;
		this.fixtureTypeRollupName=fixtureTypeRollupName;
		this.merchMethod = merchMethod;
		this.ahsSizeDesc=ahsSizeDesc;
		this.storeObj=storeObj;


	}


}
