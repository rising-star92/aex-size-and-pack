package com.walmart.aex.sp.dto.packoptimization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColorCombinationRequest {
    private Long planId;
    private String planDesc;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private String action;
    private List<String> colorCombinationIds;
    private List<ColorCombinationStyle> styles;

}
