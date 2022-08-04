package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;
import java.util.List;

@Data
public class RunPackOptRequest {
    private Long planId;
    private List<InputRequest> inputRequest;
    private String runUser;
}
