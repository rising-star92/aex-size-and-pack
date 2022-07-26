package com.walmart.aex.sp.dto.buyquantity;
import lombok.Data;

import java.util.List;

@Data
public class Lvl3Dto {
    private Integer lvl3Nbr;
    private String lvl3Desc;
    private MetricsDto metrics;
    private List<Lvl4Dto> lvl4List;
}

