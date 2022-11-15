package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;

import lombok.Data;
@Data
public class CcPackDto {
	   private String ccId;
	   private List<FixtureDto> fixtures;
	   private ColorCombinationConstraints colorCombinationConstraints;
}
