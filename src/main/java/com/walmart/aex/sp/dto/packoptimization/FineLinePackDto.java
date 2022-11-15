package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;

import lombok.Data;


@Data
public class FineLinePackDto {
	 private Integer finelineNbr;
	  private FinelineLevelConstraints finelineLevelConstraints;
	  private List<CcPackDto> customerChoices;
}
