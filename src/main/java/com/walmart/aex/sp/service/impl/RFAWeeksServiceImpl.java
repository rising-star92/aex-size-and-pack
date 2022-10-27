package com.walmart.aex.sp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.RfaWeekRequest;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.service.ChannelWeeksService;
import com.walmart.aex.sp.service.GraphQLService;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Slf4j
@Qualifier("rfaWeekService")
public class RFAWeeksServiceImpl implements ChannelWeeksService {

    @Autowired
    private GraphQLService graphQLService;
    @ManagedConfiguration
    private GraphQLProperties graphQLProperties;

    private ObjectMapper objectMapper = new ObjectMapper();

    public GraphQLResponse getWeeksByFineline(Integer fineLineNbr,
                                                   Long planId,
                                                   Integer lvl3Nbr,
                                                   Integer lvl4Nbr) {
        Map<String, String> headers = new HashMap<>();
        headers.put(WM_CONSUMER_ID, graphQLProperties.getBuyQtyRfaConsumerId());
        headers.put(WM_SVC_NAME, graphQLProperties.getBuyQtyRfaConsumerName());
        headers.put(WM_SVC_ENV, graphQLProperties.getBuyQtyRfaConsumerEnv());
        RfaWeekRequest rfaWeekRequest = new RfaWeekRequest(planId, lvl3Nbr, lvl4Nbr, fineLineNbr);
        Map<String, Object> data = objectMapper.convertValue(rfaWeekRequest, Map.class);
        GraphQLResponse graphQLResponse = null;
        try {
            graphQLResponse = graphQLService.post(graphQLProperties.getRfaWeeksUrl(),
                    graphQLProperties.getRfaWeeksQuery(),
                     headers, Map.of("rfaWeekRequest", data));
        } catch (SizeAndPackException e) {
            log.error("An Exception occured while fetching the result from RFA {}", e.getMessage());
        }
        return graphQLResponse;
    }

    @Override
    public GraphQLResponse getWeeksByCC(Integer fineLineNbr, Long planId, Integer lvl3Nbr, Integer lvl4Nbr) {
        return null;
    }
}
