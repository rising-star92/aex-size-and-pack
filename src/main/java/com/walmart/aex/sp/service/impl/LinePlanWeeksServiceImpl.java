package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.service.ChannelWeeksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Qualifier("linePlanWeekService")
public class LinePlanWeeksServiceImpl implements ChannelWeeksService {
    @Override
    public GraphQLResponse getWeeksByFineline(Integer fineLineNbr, Long planId, Integer lvl3Nbr, Integer lvl4Nbr) {
        return null;
    }

    @Override
    public GraphQLResponse getWeeksByCC(Integer fineLineNbr, Long planId, Integer lvl3Nbr, Integer lvl4Nbr) {
        return null;
    }
}
