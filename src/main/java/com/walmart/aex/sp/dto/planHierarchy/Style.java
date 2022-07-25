package com.walmart.aex.sp.dto.planHierarchy;

import com.walmart.aex.sp.dto.packOptimization.Constraints;
import com.walmart.aex.sp.dto.packOptimization.CustomerChoice;
import lombok.Data;

import java.util.List;

@Data
public class Style {
    private String styleNbr;
   
    private Constraints constraints;
    private List<CustomerChoice> customerChoices;
}
