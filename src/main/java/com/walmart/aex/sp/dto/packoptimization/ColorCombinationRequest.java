package com.walmart.aex.sp.dto.packoptimization;

import com.walmart.aex.sp.enums.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColorCombinationRequest {
    private Long planId;
    private String planDesc; //S3
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private Action action;
    private String colorCombinationId;
    private List<ColorCombinationStyle> styles;

}
