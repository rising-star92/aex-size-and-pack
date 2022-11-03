package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.currentlineplan.FinancialAttributes;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.dto.historicalmetrics.WeeksResponse;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
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
        GraphQLResponse graphQLRfaResponse;
        Payload payload;
        if (ChannelType.getChannelNameFromName(channelName).equalsIgnoreCase(ChannelType.STORE.getDescription())) {
            graphQLRfaResponse = rfaWeeksService.getWeeksByFineline(finelineNbr, planId, lvl3Nbr, lvl4Nbr);
            checkErrors(graphQLRfaResponse);
            payload = graphQLRfaResponse.getData();
            response.setFinelineNbr(finelineNbr);
            response.setStartWeek(payload.getGetRFAWeeksByFineline().getInStoreWeek());
            response.setEndWeek(payload.getGetRFAWeeksByFineline().getMarkDownWeek());
        } else {
            graphQLRfaResponse = linePlanWeeksService.getWeeksByFineline(finelineNbr, planId, lvl3Nbr, lvl4Nbr);
            checkErrors(graphQLRfaResponse);
            payload = graphQLRfaResponse.getData();
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

    private void checkErrors(GraphQLResponse graphQLRfaResponse) {
        if (graphQLRfaResponse.getData() == null && graphQLRfaResponse.getErrors() == null) {
            throw new CustomException("No data/errors found");
        } else if (graphQLRfaResponse.getErrors() != null && !graphQLRfaResponse.getErrors().isEmpty()) {
            throw new CustomException(graphQLRfaResponse.getErrors().get(0).getMessage());
        }
    }
}
