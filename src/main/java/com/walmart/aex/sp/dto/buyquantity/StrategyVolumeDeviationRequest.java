package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

import java.util.List;

@Data
public class StrategyVolumeDeviationRequest {
    private Long planId;
    private List<Integer> finelineNbrs;
}
