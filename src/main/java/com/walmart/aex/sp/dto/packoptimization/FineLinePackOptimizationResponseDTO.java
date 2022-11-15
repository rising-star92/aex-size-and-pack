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
	private Integer maxUnitsPerPack; 
	private Integer maxNbrOfPacks; 
    private String factoryId; 
	private String colorCombination; 
	private Integer singlePackInd;
	  
	 
	 
	 
	    

public FineLinePackOptimizationResponseDTO(Long planId,
			Integer finelineNbr, String ccId,  String  fixtureTypeRollupName,
										   Integer merchMethod,String ahsSizeDesc,String storeObj,
										  Integer maxUnitsPerPack, Integer maxNbrOfPacks,
										  String factoryId, String colorCombination, Integer singlePackInd) {
		this.planId = planId;
		this.finelineNbr = finelineNbr;
		this.ccId = ccId;
		this.fixtureTypeRollupName=fixtureTypeRollupName;
		this.merchMethod = merchMethod;
		this.ahsSizeDesc=ahsSizeDesc;
		this.storeObj=storeObj;
		this.maxUnitsPerPack=maxUnitsPerPack;
		this.maxNbrOfPacks=maxNbrOfPacks;
		this.factoryId=factoryId; 
		this.colorCombination=colorCombination;
		this.singlePackInd=singlePackInd;
		 
		 


	}


}
