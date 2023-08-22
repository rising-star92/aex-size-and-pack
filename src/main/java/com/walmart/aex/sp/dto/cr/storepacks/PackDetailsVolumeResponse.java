package com.walmart.aex.sp.dto.cr.storepacks;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PackDetailsVolumeResponse 
{
	 private int finelineNbr;
	 private List<StylePackVolume> stylePackVolumes;
}
