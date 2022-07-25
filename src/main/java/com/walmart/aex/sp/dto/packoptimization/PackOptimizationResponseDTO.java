package com.walmart.aex.sp.dto.packoptimization;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackOptimizationResponseDTO {
	private Long planId;
	private String channel;
	private Integer lvl0Nbr;
	private Integer lvl1Nbr;
	private Integer lvl2Nbr;
	private Integer lvl3Nbr;
	private String catSizeObj;
	private Integer lvl4Nbr;
	private String subCategorySizeObj;
	private Integer finelineNbr;
	private String fineLineSizeObj;
	private String lvl0GenDesc1;
	private String lvl1GenDesc1;
	private String lvl2GenDesc1;
	private String lvl3GenDesc1;
	private String lvl4GenDesc1;
	private String finelineDesc;


}
