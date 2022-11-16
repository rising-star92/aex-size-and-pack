package com.walmart.aex.sp.dto.isVolume;

import lombok.Data;

import java.util.List;

@Data
public class InitialSetVolumeRequest {
    private Long planId;
    private List<FinelineVolume> finelines;
}
