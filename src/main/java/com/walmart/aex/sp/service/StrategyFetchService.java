package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.APRequest;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationRequest;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
public class StrategyFetchService {
    private final GraphQLService graphQLService;

    @ManagedConfiguration
    private GraphQLProperties graphQLProperties;

    public StrategyFetchService(GraphQLService graphQLService) {
        this.graphQLService = graphQLService;
    }

    public BuyQtyResponse getBuyQtyResponseSizeProfile(BuyQtyRequest buyQtyRequest) throws SizeAndPackException
    {
        Map<String, String> headers = getHeaderForStrategy();
        Map<String, Object> data = new HashMap<>();
        data.put("sizeProfileRequest", buyQtyRequest);
        return (BuyQtyResponse) post(graphQLProperties.getSizeProfileUrl(), graphQLProperties.getSizeProfileQuery(), headers, data, Payload::getGetCcSizeClus);
    }

    public BuyQtyResponse getBuyQtyDetailsForFinelines(BuyQtyRequest buyQtyRequest) throws SizeAndPackException {

        Map<String, String> headers = getHeaderForStrategy();
        Map<String, Object> data = getBuyQtyRequest(buyQtyRequest);
        return (BuyQtyResponse) post(graphQLProperties.getSizeProfileUrl(), graphQLProperties.getBuyQtyFinelinesSizeQuery(), headers, data, Payload::getGetFinelinesWithSizeAssociation);
    }

    public BuyQtyResponse getBuyQtyDetailsForStylesCc(BuyQtyRequest buyQtyRequest, Integer finelineNbr) throws SizeAndPackException {

        Map<String, String> headers = getHeaderForStrategy();
        Map<String, Object> data = getBuyQtyRequest(buyQtyRequest);
        data.put("finelineNbr", finelineNbr);

        return (BuyQtyResponse) post(graphQLProperties.getSizeProfileUrl(), graphQLProperties.getBuyQtyStyleCcSizeQuery(), headers, data, Payload::getGetStylesCCsWithSizeAssociation);
    }

    private Object post(String url, String query, Map<String, String> headers, Map<String, Object> data, Function<Payload, ?> responseFunc) throws SizeAndPackException {
        GraphQLResponse graphQLResponse = graphQLService.post(url, query, headers, data);

        if (CollectionUtils.isEmpty(graphQLResponse.getErrors()))
            return Optional.ofNullable(graphQLResponse)
                    .stream()
                    .map(GraphQLResponse::getData)
                    .map(responseFunc)
                    .findFirst()
                    .orElse(null);

        log.error("Error returned in GraphQL call: {}", graphQLResponse.getErrors());
        return null;
    }

    public APResponse getAPRunFixtureAllocationOutput(APRequest request) throws SizeAndPackException {
        Map<String, String> headers = new HashMap<>();
        headers.put("WM_CONSUMER.ID", graphQLProperties.getAssortProductConsumerId());
        headers.put("WM_SVC.NAME", graphQLProperties.getAssortProductConsumerName());
        headers.put("WM_SVC.ENV", graphQLProperties.getAssortProductConsumerEnv());

        Map<String, Object> data = new HashMap<>();
        data.put("request", request);

        return (APResponse) post(graphQLProperties.getAssortProductUrl(), graphQLProperties.getAssortProductRFAQuery(), headers, data, Payload::getGetRFADataFromSizePack);
    }

    public BuyQtyResponse getAllCcSizeProfiles(BuyQtyRequest buyQtyRequest) throws SizeAndPackException
    {
        Map<String, String> headers = getHeaderForStrategy();
        Map<String, Object> data = new HashMap<>();
        data.put("sizeProfileRequest", buyQtyRequest);
        return (BuyQtyResponse) post(graphQLProperties.getSizeProfileUrl(), graphQLProperties.getAllCcSizeProfileQuery(), headers, data, Payload::getGetAllCcSizeClus);
    }

    private StrategyVolumeDeviationResponse getStrategyVolumeDeviation(List<StrategyVolumeDeviationRequest> strategyVolumeDeviationRequests) throws SizeAndPackException
    {
        Map<String, String> headers = getHeaderForStrategy();
        Map<String, Object> data = new HashMap<>();
        data.put("volumeDeviationRequests", strategyVolumeDeviationRequests);
        return (StrategyVolumeDeviationResponse) post(graphQLProperties.getSizeProfileUrl(), graphQLProperties.getStrategyVolumeDeviationLevel(), headers, data, Payload::getGetVolumeDeviationStrategySelection);
    }

    private Map<String, String> getHeaderForStrategy() {
        Map<String, String> headers = new HashMap<>();
        headers.put("WM_CONSUMER.ID", graphQLProperties.getSizeProfileConsumerId());
        headers.put("WM_SVC.NAME", graphQLProperties.getSizeProfileConsumerName());
        headers.put("WM_SVC.ENV", graphQLProperties.getSizeProfileConsumerEnv());
        return headers;
    }

    private Map<String, Object> getBuyQtyRequest(BuyQtyRequest buyQtyRequest)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("planId", buyQtyRequest.getPlanId());
        data.put("channel", buyQtyRequest.getChannel());
        return data;
    }

    public StrategyVolumeDeviationResponse getStrategyVolumeDeviation(Long planId, Integer finelineNbr) throws SizeAndPackException {
        List<StrategyVolumeDeviationRequest> strategyVolumeDeviationRequests = new ArrayList<>();
        StrategyVolumeDeviationRequest strategyVolumeDeviationRequest = new StrategyVolumeDeviationRequest();
        List<Integer> finelines = new ArrayList<>();

        strategyVolumeDeviationRequest.setPlanId(planId);
        finelines.add(finelineNbr);
        strategyVolumeDeviationRequest.setFinelineNbr(finelines);
        strategyVolumeDeviationRequests.add(strategyVolumeDeviationRequest);
        return getStrategyVolumeDeviation(strategyVolumeDeviationRequests);
    }

}
