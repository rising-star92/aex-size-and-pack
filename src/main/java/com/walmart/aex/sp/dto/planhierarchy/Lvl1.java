package com.walmart.aex.sp.dto.planhierarchy;

import lombok.Data;

import java.util.List;

@Data
//department
public class Lvl1 {
    private Integer lvl1Nbr;
    private String lvl1Name;
    private List<Lvl2> lvl2List;
}
