package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.dto.historicalmetrics.WeeksResponse;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.service.ChannelWeeksService;
import com.walmart.aex.sp.service.WeeksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeeksServiceImpl implements WeeksService {

    @Autowired
    @Qualifier("rfaWeekService")
    private ChannelWeeksService rfaWeeksService;

    @Autowired
    @Qualifier("linePlanWeekService")
    private ChannelWeeksService linePlanWeeksService;

    @Override
    public WeeksResponse getWeeks(String channelName, Integer finelineNbr, Long planId, Integer lvl3Nbr, Integer lvl4Nbr) {
        WeeksResponse response = new WeeksResponse();
        if (ChannelType.getChannelNameFromName(channelName).equalsIgnoreCase(ChannelType.STORE.getDescription())) {
            GraphQLResponse graphQLRfaResponse = rfaWeeksService.getWeeksByFineline(finelineNbr, planId, lvl3Nbr, lvl4Nbr);
            Payload payload = graphQLRfaResponse.getData();
            response.setFinelineNbr(finelineNbr);
            response.setStartWeek(payload.getGetRFAWeeksByFineline().getInStoreWeek());
            response.setEndWeek(payload.getGetRFAWeeksByFineline().getMarkDownWeek());
        } else {
            GraphQLResponse graphQLResponse = linePlanWeeksService.getWeeksByFineline(finelineNbr, planId, lvl3Nbr, lvl4Nbr);
            Payload payload = graphQLResponse.getData();
            response.setFinelineNbr(finelineNbr);
            response.setStartWeek(payload.getGetLinePlanFinelines().get(0).getLvl4List().get(0)
                    .getFinelines().get(0).getMetrics().getCurrent().getOnline().getFinancialAttributes().getTransactableStart());
            response.setEndWeek(payload.getGetLinePlanFinelines().get(0).getLvl4List().get(0)
                    .getFinelines().get(0).getMetrics().getCurrent().getOnline().getFinancialAttributes().getTransactableEnd());
        }
        return response;
    }
}
