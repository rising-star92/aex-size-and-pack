package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.historicalmetrics.WeeksResponse;

public interface WeeksService {
    WeeksResponse getWeeks(Integer channelId, Integer finelineNbr, Long planId, Integer lvl3Nbr, Integer lvl4Nbr);
}
