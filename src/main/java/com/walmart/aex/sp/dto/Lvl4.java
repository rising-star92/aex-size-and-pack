package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Lvl4 {

    private Integer lvl4Nbr;

    private String lvl4Name;

    private String channel;

    private Strategy strategy;

    private List<Fineline> finelines;
}
