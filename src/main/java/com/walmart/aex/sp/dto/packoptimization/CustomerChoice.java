package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
public class CustomerChoice {
    private String ccId;
    private String colorName;
    private String channel;
    private Constraints constraints;
}
