package com.walmart.aex.sp.dto.cr.storepacks;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VolumeFixtureAllocationMetrics 
{
	 private int volumeClusterId;
	 private String fixtureType;
	 private BigDecimal fixtureAllocation;
	 private int quantity;
	 private String ccId;
     private List<Store> stores;
}
