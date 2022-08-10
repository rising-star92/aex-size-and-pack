package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

import java.util.List;

@Data
public class InputRequest {
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private List<Lvl3DTO> lvl3List;
}
