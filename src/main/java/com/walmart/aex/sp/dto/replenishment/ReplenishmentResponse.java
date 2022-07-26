package com.walmart.aex.sp.dto.replenishment;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import lombok.Data;

import java.util.List;

@Data
public class ReplenishmentResponse {
    private Long planId;
    private String planDesc;
    private Integer lvl0Nbr;
    private String lvl0Desc;
    private Integer lvl1Nbr;
    private String lvl1Desc;
    private Integer lvl2Nbr;
    private String lvl2Desc;
    private List<Lvl3Dto> lvl3List;
}
