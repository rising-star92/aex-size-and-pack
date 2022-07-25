package com.walmart.aex.sp.dto.planHierarchy;

import com.walmart.aex.sp.dto.packOptimization.Constraints;
import com.walmart.aex.sp.dto.packOptimization.Fineline;
import lombok.Data;

import java.util.List;

@Data
//Sub category
public class Lvl4 {

    private Integer lvl4Nbr;

    private String lvl4Name;

    private Constraints constraints;
    
    private List<Fineline> finelines;
    
}
