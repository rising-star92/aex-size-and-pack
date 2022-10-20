package com.walmart.aex.sp.dto.initsetbumppkqty;

import lombok.Data;

@Data
public class InitSetBumpPackDTO {
	private String planAndFineline;
	private String styleNbr;
	private String customerChoice;
	private String merchMethodDesc;
	private String size;
	private Integer finalInitialSetQty;
	private Integer bumpPackQty;
}
