package com.walmart.aex.sp.dto;
import lombok.Data;

import java.util.List;

@Data
public class Lvl3Dto {
    private Integer lvl3Nbr;
    private String lvl3Desc;
    private List<Lvl4Dto> lvl4List;
}

