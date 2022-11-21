package com.walmart.aex.sp.dto.isVolume;

import lombok.Data;

import java.util.List;

@Data
public class IsPlan {
    private int inStoreWeek;
    private List<MetricsVolume> metrics;
}
