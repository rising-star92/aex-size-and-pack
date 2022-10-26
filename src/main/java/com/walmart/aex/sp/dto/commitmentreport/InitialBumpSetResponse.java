package com.walmart.aex.sp.dto.commitmentreport;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitialBumpSetResponse {
	private Integer finelineNbr;
	private List<IntialSetStyle> intialSetStyles;
	
}