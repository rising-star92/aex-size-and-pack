package com.walmart.aex.sp.dto.buyquantity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinelineVolumeDeviationDto {
    private Integer finelineId;
    private String finelineName;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Long planId;
    private String volumeDeviationLevel;
}
