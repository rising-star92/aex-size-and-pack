package com.walmart.aex.sp.dto.packoptimization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BumpSetDto {

	private Integer setNbr;
	private Integer wmYearWeek;
	private String weekDesc;
	private Integer bsUnits;
	private Integer totalUnits;

	@Override
	public String toString() {
		return "BumpSetDto{" +
				"setNbr=" + setNbr +
				", wmYearWeek=" + wmYearWeek +
				", weekDesc=" + weekDesc +
				", bsUnits=" + bsUnits +
				", totalUnits=" + totalUnits +
				'}';
	}
}
