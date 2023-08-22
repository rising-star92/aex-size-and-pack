package com.walmart.aex.sp.dto.cr.storepacks;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public final class VolumeClusterInfo 
{
	private final Integer clusterId;
	private final String fixtureType;
	private final BigDecimal fixtureAllocation;
}
