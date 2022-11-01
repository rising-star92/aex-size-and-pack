package com.walmart.aex.sp.dto.bqfp;


import lombok.Data;

@Data
public class WeeksDTO {
    private Integer yearWkNbr;
    private Integer wmYearWk;
    private Integer wmYearWkLy;
    private Integer wmYearWkLly;
    private String fiscalWeekDesc;
}
