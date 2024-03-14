package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.StoreClusterMap;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.dto.store.cluster.ClusterAttributes;
import com.walmart.aex.sp.dto.store.cluster.ClusterInfoRequest;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.properties.StoreClusterProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Slf4j
public class StoreClusterServiceImpl implements StoreClusterService {

    private final GraphQLService graphQLService;

    @ManagedConfiguration
    public StoreClusterProperties storeClusterProperties;

    @ManagedConfiguration
    private GraphQLProperties graphQLProperties;

    public StoreClusterServiceImpl(GraphQLService graphQLService) {
        this.graphQLService = graphQLService;
    }

    @Override
    @Cacheable(value = "aex_po_store_grouping",
            cacheManager = "memCacheManager",
            condition = "#root.target.storeClusterProperties.isPOStoreClusterEnabled()",
            unless = "#result == null || #result.size() == 0")
    public StoreClusterMap fetchPOStoreClusterGrouping(String season, String fiscalYear) throws SizeAndPackException {
        log.info("Fetching PO Store Cluster Grouping from StoreClusterAPI...");
        StoreClusterMap storeClusterMap = new StoreClusterMap();
        if (Boolean.TRUE.equals(storeClusterProperties.isPOStoreClusterEnabled())) {
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

            ClusterAttributes clusterAttributes = new ClusterAttributes();
            clusterAttributes.setGroupingType("po-grouping");
            clusterAttributes.setSeason(season);
            clusterAttributes.setFiscalYear(fiscalYear);

            ClusterInfoRequest clusterInfoRequest = new ClusterInfoRequest();
            clusterInfoRequest.setAppType("aex-fashion");
            clusterInfoRequest.setClusterAttributes(clusterAttributes);

            Map<String, Object> request = new HashMap<>();
            request.put("clusterInfoFindAllInput", clusterInfoRequest);

            GraphQLResponse response = graphQLService.post(storeClusterProperties.getStoreClusterUrl(), query, headers, request);

            Optional.ofNullable(response)
                    .map(GraphQLResponse::getData)
                    .map(Payload::getClusterInfo)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(clusterInfo -> storeClusterMap.put(clusterInfo.getClusterName(), clusterInfo.getStoreList()));
        }
        return storeClusterMap;
    }

    @Override
    @CacheEvict(value = "aex_po_store_grouping",
            cacheManager = "memCacheManager",
            allEntries = true)
    public void invalidateStoreClusterCache() {
        log.info("Store Cluster Cache Evicted...");
    }

}
