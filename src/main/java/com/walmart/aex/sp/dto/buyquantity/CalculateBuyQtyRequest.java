package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

import java.util.List;

@Data
public class CalculateBuyQtyRequest {
    private Long planId;
    private String userId;
    private String seasonCode;
    private Integer fiscalYear;
    private String channel;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private List<Lvl3Dto> lvl3List;
}
