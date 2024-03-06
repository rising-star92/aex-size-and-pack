package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.dto.store.cluster.ClusterInfo;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.properties.StoreClusterProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Slf4j
public class StoreClusterService {

    private final GraphQLService graphQLService;

    @ManagedConfiguration
    private StoreClusterProperties storeClusterProperties;

    @ManagedConfiguration
    private GraphQLProperties graphQLProperties;

    public StoreClusterService(GraphQLService graphQLService) {
        this.graphQLService = graphQLService;
    }

    // TODO - Add Caching
    public Map<String, List<Integer>> fetchPOStoreClusterGrouping(String season, String fiscalYear) throws SizeAndPackException {
        log.info("Fetching PO Store Cluster Grouping from StoreClusterAPI...");
        String query;
        try {
            query = new String(Objects.requireNonNull(getClass().getClassLoader()
                    .getResourceAsStream("graphql_queries/store_cluster_query.txt")).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("IO exception occurred while reading store cluster query", e);
            throw new CustomException(MessageFormat.format("Exception in reading store cluster query - {0}", e));
        }

        Map<String, String> headers = new HashMap<>();
        headers.put(WM_CONSUMER_ID, graphQLProperties.getStoreClusterConsumerId());
        headers.put(WM_SVC_NAME, graphQLProperties.getStoreClusterConsumerName());
        headers.put(WM_SVC_ENV, graphQLProperties.getStoreClusterConsumerEnv());

        // TODO - Change to query using season & fiscal year
        Map<String, Object> request = new HashMap<>();
        request.put("createdBy", "v0r00n2");

        GraphQLResponse response = graphQLService.post(storeClusterProperties.getStoreClusterUrl(), query, headers, request);

        return Optional.ofNullable(response)
                .map(GraphQLResponse::getData)
                .map(Payload::getClusterInfo)
                .map(clusterList -> clusterList.stream()
                        .collect(Collectors.toMap(ClusterInfo::getClusterName, ClusterInfo::getStoreList)))
                .orElse(null);
    }

}
