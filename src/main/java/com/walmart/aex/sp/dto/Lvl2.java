package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Lvl2 {
    private Integer lvl2Nbr;

    private String lvl2Name;

    private Strategy strategy;

    private List<Lvl3> lvl3List;
}
