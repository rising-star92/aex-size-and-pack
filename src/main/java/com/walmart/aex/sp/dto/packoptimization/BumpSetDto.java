package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
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
