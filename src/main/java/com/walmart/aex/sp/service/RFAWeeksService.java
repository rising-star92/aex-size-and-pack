package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.gql.GraphQLRfaResponse;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BQFPServiceProperties;
import com.walmart.aex.sp.properties.GraphQLProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RFAWeeksService {

    @Autowired
    private GraphQLService graphQLService;
    @ManagedConfiguration
    private GraphQLProperties graphQLProperties;

    @ManagedConfiguration
    private BQFPServiceProperties bqfpServiceProperties;

    public GraphQLRfaResponse getRFAWeeksByFineline(Integer fineLineNbr,
                                                   Integer planId,
                                                   Integer lvl3Nbr,
                                                   Integer lvl4Nbr) {
        Map<String, String> headers = new HashMap<>();
        headers.put("WM_CONSUMER.ID", graphQLProperties.getAssortProductConsumerId());
        headers.put("WM_SVC.NAME", graphQLProperties.getAssortProductConsumerName());
        headers.put("WM_SVC.ENV", graphQLProperties.getAssortProductConsumerEnv());
        headers.put("wm_consumer.id", bqfpServiceProperties.getConsumerId());
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> variablevalues = new HashMap<>();
        variablevalues.put("planId", planId);
        variablevalues.put("finelineNbr", fineLineNbr);
        variablevalues.put("lvl3Nbr", lvl3Nbr);
        variablevalues.put("lvl4Nbr", lvl4Nbr);
        data.put("rfaWeekRequest", variablevalues);
        GraphQLRfaResponse graphQLRfaResponse = null;
        try {
            graphQLRfaResponse = graphQLService.post(graphQLProperties.getRfaWeeksUrl(),
                    graphQLProperties.getRfaWeeksQuery(),
                     headers, data, GraphQLRfaResponse.class);
        } catch (SizeAndPackException e) {
            log.error("An Exception occured while fetching the result from RFA {}", e.getMessage());
        }
        return graphQLRfaResponse;
    }
}
