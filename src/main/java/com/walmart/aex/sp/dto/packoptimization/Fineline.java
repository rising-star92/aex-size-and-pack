package com.walmart.aex.sp.dto.packoptimization;

import com.walmart.aex.sp.dto.planhierarchy.Style;
import lombok.Data;

import java.util.List;

@Data
public class Fineline {
    private Integer finelineNbr;
    private String finelineName;
    private String altFinelineName;
    private String channel;
    private String packOptimizationStatus;
    private Constraints constraints;
    private List<Style> styles;
}
