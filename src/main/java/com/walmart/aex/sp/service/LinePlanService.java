package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.currentlineplan.Fineline;
import com.walmart.aex.sp.dto.currentlineplan.LikeAssociation;
import com.walmart.aex.sp.dto.currentlineplan.Lvl3;
import com.walmart.aex.sp.dto.currentlineplan.Lvl4;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Slf4j
public class LinePlanService {

    private final GraphQLService graphQLService;

    @ManagedConfiguration
    private GraphQLProperties graphQLProperties;

    public LinePlanService(GraphQLService graphQLService) {
        this.graphQLService = graphQLService;
    }

    public LikeAssociation getLikeAssociation(Long planId, Integer finelineNbr) throws SizeAndPackException {
        Map<String, String> headers = getHeaders();
        Map<String, Object> data = new HashMap<>();
        data.put("planId", planId);
        data.put("finelineIds", Collections.singletonList(finelineNbr));
        try {
            GraphQLResponse graphQLResponse = graphQLService.post(graphQLProperties.getLinePlanUrl(),
                    graphQLProperties.getLinePlanLikeFinelineQuery(),
                    headers, data);
            Optional<LikeAssociation> response = graphQLResponse.getData().getGetLinePlanFinelines().stream()
                    .map(Lvl3::getLvl4List)
                    .flatMap(Collection::stream)
                    .map(Lvl4::getFinelines)
                    .flatMap(Collection::stream)
                    .map(Fineline::getLikeAssociation)
                    .filter(Objects::nonNull)
                    .findFirst();
            return response.orElse(null);
        } catch (SizeAndPackException e) {
            log.error("An Exception occurred while fetching the result from LinePlan {}", e.getMessage());
            throw new SizeAndPackException("An Exception occurred while fetching the result from LinePlan");
        }
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(WM_CONSUMER_ID, graphQLProperties.getBuyQtyLinePlanConsumerId());
        headers.put(WM_SVC_NAME, graphQLProperties.getBuyQtyLinePlanConsumerName());
        headers.put(WM_SVC_ENV, graphQLProperties.getBuyQtyLinePlanConsumerEnv());
        return headers;
    }
}
