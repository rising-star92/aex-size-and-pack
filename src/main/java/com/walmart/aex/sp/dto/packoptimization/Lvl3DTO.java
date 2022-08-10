package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

import java.util.List;

@Data
public class Lvl3DTO {
    private Integer lvl3Nbr;
    private List<Lvl4DTO> lvl4DTOList;
}
