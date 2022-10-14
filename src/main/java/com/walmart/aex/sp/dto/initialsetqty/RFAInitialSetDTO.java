package com.walmart.aex.sp.dto.initialsetqty;

import lombok.Data;

@Data
public class RFAInitialSetDTO {

	private String planAndFineline;
	private String styleNbr;
	private String customerChoice;
	private String merchMethodDesc;
	private String size;
	private Integer finalInitialSetQty;
}
