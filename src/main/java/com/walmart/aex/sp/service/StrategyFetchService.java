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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
public class StrategyFetchService {
    private final GraphQLService graphQLService;

    @ManagedConfiguration
    GraphQLProperties graphQLProperties;

    StrategyFetchService(GraphQLService graphQLService) {
        this.graphQLService = graphQLService;
    }

    public BuyQtyResponse getBuyQtyResponseSizeProfile(BuyQtyRequest buyQtyRequest) throws SizeAndPackException
    {
        Map<String, String> headers = getHeaderForStrategy();
        Map<String, Object> data = new HashMap<>();
        data.put("sizeProfileRequest", buyQtyRequest);
        return (BuyQtyResponse) post(graphQLProperties.getSizeProfileUrl(), "query ($sizeProfileRequest:SizeProfileRequest!)\n {\n getCcSizeClus(sizeProfileRequest:  $sizeProfileRequest)\n {\n planId\n lvl0Nbr\n lvl1Nbr\n lvl2Nbr\n lvl3List{\n lvl3Nbr\n lvl4List{\n lvl4Nbr\n finelines{\n finelineNbr\n styles{\n styleNbr\n customerChoices{\n ccId\n clusters{\n clusterID\n sizes{\n ahsSizeId\n sizeDesc\n metrics{\n sizeProfilePct\n adjSizeProfilePct\n avgSizeProfilePct\n adjAvgSizeProfilePct\n }}}}}}}}}}", headers, data, Payload::getGetCcSizeClus);
    }

    public BuyQtyResponse getBuyQtyDetailsForFinelines(BuyQtyRequest buyQtyRequest) throws SizeAndPackException {

        Map<String, String> headers = getHeaderForStrategy();
        Map<String, Object> data = getBuyQtyRequest(buyQtyRequest);
        return (BuyQtyResponse) post(graphQLProperties.getSizeProfileUrl(), "query ($planId:Int!,$channel:String!)\n {\n getFinelinesWithSizeAssociation(planId : $planId, channel : $channel)\n {\n planId\n lvl0Nbr\n lvl1Nbr\n lvl2Nbr\n lvl3List{\n lvl3Nbr\n lvl4List{\n lvl4Nbr\n finelines{\n finelineNbr\n }}}}}", headers, data, Payload::getGetFinelinesWithSizeAssociation);
    }

    public BuyQtyResponse getBuyQtyDetailsForStylesCc(BuyQtyRequest buyQtyRequest, Integer finelineNbr) throws SizeAndPackException {

        Map<String, String> headers = getHeaderForStrategy();
        Map<String, Object> data = getBuyQtyRequest(buyQtyRequest);
        data.put("finelineNbr", finelineNbr);

        return (BuyQtyResponse) post(graphQLProperties.getSizeProfileUrl(), "query ($planId:Int!,$channel:String!,$finelineNbr:Int!)\n {\n getStylesCCsWithSizeAssociation(planId : $planId, channel : $channel, finelineNbr: $finelineNbr)\n {\n planId\n lvl0Nbr\n lvl1Nbr\n lvl2Nbr\n lvl3List{\n lvl3Nbr\n lvl4List{\n lvl4Nbr\n finelines{\n finelineNbr\n styles{\n styleNbr\n customerChoices{\n ccId\n }}}}}}}", headers, data, Payload::getGetStylesCCsWithSizeAssociation);
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

        return (APResponse) post(graphQLProperties.getAssortProductUrl(), "query($request:RFASizePackRequest!){getRFADataFromSizePack(rfaSizePackRequest:$request){rfaSizePackData{rpt_lvl_0_nbr rpt_lvl_1_nbr rpt_lvl_2_nbr rpt_lvl_3_nbr rpt_lvl_4_nbr fineline_nbr style_nbr customer_choice fixture_type fixture_group color_family size_cluster_id volume_group_cluster_id store_list store_cnt plan_id_partition}}}", headers, data, Payload::getGetRFADataFromSizePack);
    }

    public BuyQtyResponse getAllCcSizeProfiles(BuyQtyRequest buyQtyRequest) throws SizeAndPackException
    {
        Map<String, String> headers = getHeaderForStrategy();
        Map<String, Object> data = new HashMap<>();
        data.put("sizeProfileRequest", buyQtyRequest);
        return (BuyQtyResponse) post(graphQLProperties.getSizeProfileUrl(), "query ($sizeProfileRequest:SizeProfileRequest!)\n {\n getAllCcSizeClus(sizeProfileRequest:  $sizeProfileRequest)\n {\n planId\n lvl0Nbr\n lvl1Nbr\n lvl2Nbr\n lvl3List{\n lvl3Nbr\n lvl4List{\n lvl4Nbr\n finelines{\n finelineNbr\n merchMethods{\n fixtureTypeRollupId\n merchMethod\n merchMethodCode\n }\n metrics{\n sizeProfilePct\n adjSizeProfilePct\n avgSizeProfilePct\n adjAvgSizeProfilePct\n }\n clusters{\n clusterID\n sizes{\n ahsSizeId\n sizeDesc\n metrics{\n sizeProfilePct\n adjSizeProfilePct\n avgSizeProfilePct\n adjAvgSizeProfilePct\n }\n }\n }\n styles{\n styleNbr\n metrics{\n sizeProfilePct\n adjSizeProfilePct\n avgSizeProfilePct\n adjAvgSizeProfilePct\n }\n clusters{\n clusterID\n sizes{\n ahsSizeId\n sizeDesc\n metrics{\n sizeProfilePct\n adjSizeProfilePct\n avgSizeProfilePct\n adjAvgSizeProfilePct\n }\n }\n }\n customerChoices{\n ccId\n metrics{\n sizeProfilePct\n adjSizeProfilePct\n avgSizeProfilePct\n adjAvgSizeProfilePct\n }\n clusters{\n clusterID\n sizes{\n ahsSizeId\n sizeDesc\n metrics{\n sizeProfilePct\n adjSizeProfilePct\n avgSizeProfilePct\n adjAvgSizeProfilePct\n }}}}}}}}}}", headers, data, Payload::getGetAllCcSizeClus);
    }

    public StrategyVolumeDeviationResponse getStrategyVolumeDeviation(List<StrategyVolumeDeviationRequest> strategyVolumeDeviationRequests) throws SizeAndPackException
    {
        Map<String, String> headers = getHeaderForStrategy();
        Map<String, Object> data = new HashMap<>();
        data.put("volumeDeviationRequests", strategyVolumeDeviationRequests);
        return (StrategyVolumeDeviationResponse) post(graphQLProperties.getSizeProfileUrl(), "query($volumeDeviationRequests:[VolumeDeviationRequests]) {getVolumeDeviationStrategySelection (request: {volumeDeviationRequestsList: $volumeDeviationRequests }) { finelines { finelineId finelineName lvl0Nbr lvl1Nbr lvl2Nbr lvl3Nbr lvl4Nbr planId volumeDeviationLevel } } }", headers, data, Payload::getGetVolumeDeviationStrategySelection);
    }

    public Map<String, String> getHeaderForStrategy() {
        Map<String, String> headers = new HashMap<>();
        headers.put("WM_CONSUMER.ID", graphQLProperties.getSizeProfileConsumerId());
        headers.put("WM_SVC.NAME", graphQLProperties.getSizeProfileConsumerName());
        headers.put("WM_SVC.ENV", graphQLProperties.getSizeProfileConsumerEnv());
        return headers;
    }

    public Map<String, Object> getBuyQtyRequest(BuyQtyRequest buyQtyRequest)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("planId", buyQtyRequest.getPlanId());
        data.put("channel", buyQtyRequest.getChannel());
        return data;
    }

}
