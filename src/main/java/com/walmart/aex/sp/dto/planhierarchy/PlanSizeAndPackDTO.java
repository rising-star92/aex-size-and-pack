package com.walmart.aex.sp.dto.planhierarchy;

import lombok.Data;

import java.util.List;

@Data
public class PlanSizeAndPackDTO {
    private Long planId;
    private String planDesc;
    private Integer lvl0Nbr;
    private String lvl0Name;
    private List<Lvl1> lvl1List;
}
