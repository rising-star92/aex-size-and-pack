package com.walmart.aex.sp.dto.packoptimization;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RunOptimization {
    private String name;
    private List<String> runStatusLongDesc;
    private Date startTs;
    private Date endTs;
    private Integer runStatusCode;
}
