package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;



import lombok.Data;
@Data
public class CcPackDto {
	   private String ccId;
	   private List<FixtureDto> fixtures;
}
