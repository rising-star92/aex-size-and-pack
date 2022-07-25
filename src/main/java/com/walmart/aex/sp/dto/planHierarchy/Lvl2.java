package com.walmart.aex.sp.dto.planHierarchy;

import lombok.Data;

import java.util.List;

@Data
//category group
public class Lvl2 {
    private Integer lvl2Nbr;

    private String lvl2Name;

    private List<Lvl3> lvl3List;
}
