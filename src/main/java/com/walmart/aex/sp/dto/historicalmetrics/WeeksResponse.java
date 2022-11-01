package com.walmart.aex.sp.dto.historicalmetrics;

import com.walmart.aex.sp.dto.bqfp.WeeksDTO;
import lombok.Data;

@Data
public class WeeksResponse {
    private Integer finelineNbr;
    private WeeksDTO startWeek;
    private WeeksDTO endWeek;
}
