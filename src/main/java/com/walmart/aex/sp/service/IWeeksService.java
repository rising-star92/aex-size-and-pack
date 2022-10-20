package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.RfaWeeksResponse;
import com.walmart.aex.sp.dto.gql.GraphQLRfaResponse;

public interface IWeeksService {
    RfaWeeksResponse getWeeks(Integer channelId, Integer finelineNbr, Integer planId, Integer lvl3Nbr, Integer lvl4Nbr);
}
