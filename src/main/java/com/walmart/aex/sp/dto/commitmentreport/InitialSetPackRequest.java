package com.walmart.aex.sp.dto.commitmentreport;

import lombok.Data;

@Data
public class InitialSetPackRequest {
	private Integer planId;
	private String interval;
	private Integer fiscalYear;
	private Integer finelineNbr;
}
