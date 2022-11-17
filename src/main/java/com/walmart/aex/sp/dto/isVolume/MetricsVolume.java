package com.walmart.aex.sp.dto.isVolume;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MetricsVolume {
    private List<Integer> stores;
    private int volumeClusterId;
    private String fixtureType;
    private BigDecimal  fixtureAllocation;
    private int quantity;
}
