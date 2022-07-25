package com.walmart.aex.sp.dto.planhierarchy;

import com.walmart.aex.sp.dto.packoptimization.Constraints;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
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
