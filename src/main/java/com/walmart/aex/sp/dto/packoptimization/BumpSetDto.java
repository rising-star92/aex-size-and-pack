package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;

import lombok.Data;

@Data
public class BumpSetDto {

	private Integer setNbr;
	private Integer wmYearWeek;
	private Integer weekDesc;
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
