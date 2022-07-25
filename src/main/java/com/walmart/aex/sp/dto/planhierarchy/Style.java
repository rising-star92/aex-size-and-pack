package com.walmart.aex.sp.dto.planhierarchy;

import com.walmart.aex.sp.dto.packoptimization.Constraints;
import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import lombok.Data;

import java.util.List;

@Data
public class Style {
    private String styleNbr;
   
    private Constraints constraints;
    private List<CustomerChoice> customerChoices;
}
