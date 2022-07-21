package com.walmart.aex.sp.dto;
import lombok.Data;

import java.util.List;

@Data
public class lvl3Dto {
    private Integer lvl3Nbr;
    private String lvl3Desc;
   private List<lvl4Dto> lvl4List;
}

