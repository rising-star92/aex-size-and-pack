package com.walmart.aex.sp.dto;

import lombok.Data;

@Data
public class FiscalWeek {
    private Integer wmYearWeek;
    private Integer dwWeekId;
    private String fiscalWeekDesc;
}
