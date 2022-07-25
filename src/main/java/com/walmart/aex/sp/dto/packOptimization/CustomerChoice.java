package com.walmart.aex.sp.dto.packOptimization;

import com.walmart.aex.sp.dto.packOptimization.Constraints;
import lombok.Data;

@Data
public class CustomerChoice {
    private String ccId;
    private String colorName;
    private Constraints constraints;
}
