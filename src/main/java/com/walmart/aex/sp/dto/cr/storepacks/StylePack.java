package com.walmart.aex.sp.dto.cr.storepacks;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public final class StylePack 
{
	private final String styleId;
	private final String packId;
	private final String merchMethod;
	private final Integer bumpPackNbr;
	private final String finelineDesc;
}
