package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.currentlineplan.FinancialAttributes;
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
import org.springframework.util.ObjectUtils;

import java.util.Optional;

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
            Optional<FinancialAttributes> financialAttributes = getFinancialAttributes(payload);
            if(financialAttributes.isPresent()) {
                response.setFinelineNbr(finelineNbr);
                response.setStartWeek(financialAttributes.get().getTransactableStart());
                response.setEndWeek(financialAttributes.get().getTransactableEnd());
            }
        }
        return response;
    }

    private Optional<FinancialAttributes> getFinancialAttributes(Payload payload) {
        Optional<FinancialAttributes> response = Optional.empty();
        if(!ObjectUtils.isEmpty(payload)) {
            response = Optional.of(payload.getGetLinePlanFinelines().stream().findFirst()
                    .get().getLvl4List().stream().findFirst().get().getFinelines().stream().findFirst()
                    .get().getMetrics().getCurrent().getOnline().getFinancialAttributes());
        }
        return response;
    }
}
