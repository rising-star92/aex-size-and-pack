package com.walmart.aex.sp.dto.packoptimization;

import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import lombok.Data;

import java.util.List;

@Data
public class InputRequest {
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private List<Lvl3Dto> lvl3List;
}
