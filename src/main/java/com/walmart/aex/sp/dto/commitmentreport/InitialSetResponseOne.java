package com.walmart.aex.sp.dto.commitmentreport;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitialSetResponseOne {
	private Integer finelineNbr;
	  private String styleId;
	  private List<InitialSetPlan> initialSetPlan;
	
}