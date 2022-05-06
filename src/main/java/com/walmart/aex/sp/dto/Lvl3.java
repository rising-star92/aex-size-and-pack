package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Lvl3 {

    private Integer lvl3Nbr;

    private String lvl3Name;

    private String channel;

    private Strategy strategy;

    private List<Lvl4> lvl4List;

}
