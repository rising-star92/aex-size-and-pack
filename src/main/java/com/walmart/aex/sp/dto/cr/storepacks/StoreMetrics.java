package com.walmart.aex.sp.dto.cr.storepacks;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoreMetrics
{
	private Integer store;
	private Integer multiplier;
    private Integer qty;
}
