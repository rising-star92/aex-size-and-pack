package com.walmart.aex.sp.dto.packoptimization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DCinboundReplenishment {
    private Integer replnWeek;
    private String replnWeekDesc;
    private Integer replnUnits;
    private Integer adjReplnUnits;
    private Integer remainingUnits;
}
