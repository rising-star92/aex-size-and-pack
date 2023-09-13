package com.walmart.aex.sp.service.impl;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Slf4j
@Qualifier("linePlanWeekService")
public class LinePlanWeeksServiceImpl implements ChannelWeeksService {

    @Autowired
    private GraphQLService graphQLService;

    @ManagedConfiguration
    private GraphQLProperties graphQLProperties;

    @Override
    public GraphQLResponse getWeeksByFineline(Integer fineLineNbr, Long planId, Integer lvl3Nbr, Integer lvl4Nbr) {
        Map<String, String> headers = new HashMap<>();
        headers.put(WM_CONSUMER_ID, graphQLProperties.getBuyQtyLinePlanConsumerId());
        headers.put(WM_SVC_NAME, graphQLProperties.getBuyQtyLinePlanConsumerName());
        headers.put(WM_SVC_ENV, graphQLProperties.getBuyQtyLinePlanConsumerEnv());
        Map<String, Object> data = new HashMap<>();
        data.put("planId", planId);
        data.put("finelineIds", Collections.singletonList(fineLineNbr));
        GraphQLResponse graphQLResponse = null;
        try {
            graphQLResponse = graphQLService.post(graphQLProperties.getLinePlanUrl(),
                    graphQLProperties.getLinePlanWeeksQuery(),
                    headers, data);
        } catch (SizeAndPackException e) {
            log.error("An Exception occured while fetching the result from LinePlanWeeks {}", e.getMessage());
        }
        return graphQLResponse;
    }

    @Override
    public GraphQLResponse getWeeksByCC(Integer fineLineNbr, Long planId, Integer lvl3Nbr, Integer lvl4Nbr) {
        return null;
    }
}
