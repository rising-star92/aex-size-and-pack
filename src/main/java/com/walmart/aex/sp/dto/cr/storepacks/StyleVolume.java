package com.walmart.aex.sp.dto.cr.storepacks;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StyleVolume 
{
	private String styleId;
	private String packId;
	private List<VolumeFixtureAllocationMetrics> metrics;
}
