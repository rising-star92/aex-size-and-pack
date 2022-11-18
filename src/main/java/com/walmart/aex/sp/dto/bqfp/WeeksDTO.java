package com.walmart.aex.sp.dto.bqfp;


import lombok.Data;

import java.io.Serializable;

@Data
public class WeeksDTO implements Serializable {
    private Integer yearWkNbr;
    private Integer wmYearWk;
    private Integer wmYearWkLy;
    private Integer wmYearWkLly;
    private String fiscalWeekDesc;
}
