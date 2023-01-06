package com.walmart.aex.sp.dto.buyquantity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StrategyVolumeDeviationResponse {
    private List<FinelineVolumeDeviationDto> finelines;
}