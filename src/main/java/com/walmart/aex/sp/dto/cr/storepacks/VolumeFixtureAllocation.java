package com.walmart.aex.sp.dto.cr.storepacks;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@Builder
public final class VolumeFixtureAllocation 
{
	 private final int volumeClusterId;
	 private final String fixtureType;
	 private final BigDecimal fixtureAllocation;
	 private final String ccId;
}
