package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Fineline {
    private Integer finelineNbr;
    private String finelineName;
    private String packOptimizationStatus;
    private Constraints constraints;
    private List<Style> styles;
    
}
