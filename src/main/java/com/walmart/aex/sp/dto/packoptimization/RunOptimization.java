package com.walmart.aex.sp.dto.packoptimization;


import lombok.Data;

import java.util.Date;

@Data
public class RunOptimization {
    private String name;
    private String returnMessage;
    private Date startTs;
    private Date endTs;
    private Integer runStatusCode;
}
