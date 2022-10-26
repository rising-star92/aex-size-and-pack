package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.gql.GraphQLResponse;

public interface ChannelWeeksService {
    GraphQLResponse getWeeksByFineline(Integer fineLineNbr, Long planId, Integer lvl3Nbr, Integer lvl4Nbr);
    GraphQLResponse getWeeksByCC(Integer fineLineNbr, Long planId, Integer lvl3Nbr, Integer lvl4Nbr);
}
