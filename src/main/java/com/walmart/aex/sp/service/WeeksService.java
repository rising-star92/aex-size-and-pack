package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.RfaWeeksResponse;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeeksService implements IWeeksService{
    @Autowired
    RFAWeeksService rfaWeeksService;
    @Override
    public RfaWeeksResponse getWeeks(Integer channelId, Integer finelineNbr, Integer planId, Integer lvl3Nbr, Integer lvl4Nbr) {
        if (channelId == 2) {
            GraphQLResponse graphQLRfaResponse = rfaWeeksService.getRFAWeeksByFineline(finelineNbr, planId, lvl3Nbr, lvl4Nbr);
            Payload payload = graphQLRfaResponse.getData();
            return payload.getGetRFAWeeksByFineline();
        } else {
            return null;
        }
    }
}
