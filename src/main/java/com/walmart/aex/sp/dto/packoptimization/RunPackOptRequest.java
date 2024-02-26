package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
public class RunPackOptRequest {
    private Long planId;
    private InputRequest inputRequest;
    private String runUser;
}
