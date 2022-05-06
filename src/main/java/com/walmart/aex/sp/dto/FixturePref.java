package com.walmart.aex.sp.dto;

import lombok.Data;

@Data
public class FixturePref {
    private String type;
    private Integer orderPref;
    private Integer belowMin;
    private Integer belowMax;
    private Integer fgStart;
    private Integer fgEnd;
    private Integer fgMin;
    private Integer fgMax;
    private Integer maxCcs;
}
