package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlanStrategyDTO {
    private Long planId;
    private String planDesc;
    private Integer lvl0Nbr;
    private String lvl0Name;
    private List<Lvl1> lvl1List;
}
