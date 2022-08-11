package com.walmart.aex.sp.dto.planhierarchy;

import java.util.List;

import com.walmart.aex.sp.dto.packoptimization.Constraints;
import lombok.Data;

@Data
public class Lvl3 {
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private String lvl3Name;
    private Constraints constraints;
    private List<Lvl4> lvl4List;

}
