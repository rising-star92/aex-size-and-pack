package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
public class DCinboundReplenishment {
    private Integer replnWeek;
    private String replnWeekDesc;
    private Integer replnUnits;
    private Integer remainingUnits;
}
