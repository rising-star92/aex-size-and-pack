package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;
import lombok.Data;

@Data
public class FixtureDto {

	private String fixtureType;
	private String merchMethod;
	private List <SizePackDto> sizes;


}
