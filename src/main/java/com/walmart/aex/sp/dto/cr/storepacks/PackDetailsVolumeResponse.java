package com.walmart.aex.sp.dto.cr.storepacks;

import java.util.List;

import lombok.Data;

@Data
public class PackDetailsVolumeResponse 
{
	 private int finelineNbr;
	 private List<StyleVolume> styleVolumes;
}
