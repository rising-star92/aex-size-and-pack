package com.walmart.aex.sp.dto;
import lombok.Data;

import java.util.List;

@Data
public class lvl3ListDto {
    private Integer lvl3Nbr;
    private String lvl3Desc;
   private List<lvl4ListDto> lvl4List;
}

