package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.walmart.aex.sp.dto.storedistribution.PackData;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionDTO;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionData;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;

import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BigQueryStoreDistributionService {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@ManagedConfiguration
	BigQueryConnectionProperties bigQueryConnectionProperties;

	public StoreDistributionData getStoreDistributionData(PackData packData) {
		StoreDistributionData storeDistributionData = new StoreDistributionData();
		List<StoreDistributionDTO> storeDistributionList = new ArrayList<>();

		try {
			String projectId = bigQueryConnectionProperties.getRFAProjectId();
			String parquetDatasetName = bigQueryConnectionProperties.getRFADataSetName();
			String parquetTable = bigQueryConnectionProperties.getRFACCStageTable();
			String assortmentMLProjectId = bigQueryConnectionProperties.getMLProjectId();
			String packOptDatasetName = bigQueryConnectionProperties.getMLDataSetName();
			String packOptTable = bigQueryConnectionProperties.getRFASPPackOptTableName();
			String parquetTableName = projectId + "." + parquetDatasetName + "." + parquetTable;
			String packOptOutputTableName = assortmentMLProjectId + "." + packOptDatasetName + "." + packOptTable;
			Long planId = packData.getPlanId();
			Integer fineline = packData.getFinelineNbr();
			Long inStoreWeek = packData.getInStoreWeek();
			String packId = packData.getPackId();
			String planAndFineline = String.valueOf(planId) + "_" + String.valueOf(fineline);
			String storeDistributionQuery = getStoreDistributionQuery(parquetTableName, packOptOutputTableName,
					planAndFineline, planId, fineline, inStoreWeek, packId);
			BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
			QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(storeDistributionQuery).build();
			TableResult results = bigQuery.query(queryConfig);
			results.iterateAll().forEach(rows -> rows.forEach(row -> {
				try {
					StoreDistributionDTO storeDistributionDTO = objectMapper.readValue(row.getValue().toString(),
							StoreDistributionDTO.class);
					storeDistributionList.add(storeDistributionDTO);
					storeDistributionData.setStoreDistributionList(storeDistributionList);
				} catch (JsonProcessingException e) {
					log.error("Error while mapping the gcp table response data \n", e);
				}
			}));
			log.info("Query performed successfully.");
		} catch (Exception e) {
			log.error("Exception details are ", e);
		}

		log.info("results: {}", storeDistributionData);
		return storeDistributionData;
	}

	private String getStoreDistributionQuery(String parquetTableName, String packOptOutputTableName,
			String planAndFineline, Long planId, Integer fineline, Long inStoreWeek, String packId) {
		return "WITH MyTable AS ((select distinct RFA.finelineNbr, RFA.styleNbr,RFA.inStoreWeek, SP.packId, SP.store, (SP.initialPackMultiplier) AS initialPackMultiplier from "
				+ "(select fineline as finelineNbr, reverse( SUBSTR(REVERSE(trim(cc)), STRPOS(REVERSE(trim(cc)), \"_\")+1)) as styleNbr,trim(cc) as cc, CAST(store AS INTEGER) as store, min(week) as inStoreWeek "
				+ "from `" + parquetTableName + "` as RFA where plan_id_partition=" + planId + " and fineline="
				+ fineline + " and final_alloc_space>0"
				+ " group by finelineNbr, styleNbr,cc,store "
				+ "order by finelineNbr, styleNbr,cc, inStoreWeek, store ) as RFA " + "join "
				+ "(SELECT trim(SP.ProductCustomerChoice)as cc,SP.store, SP.SPPackID as packId, SP.MerchMethod as merch_method, SP.size, "
				+ "SP.SPInitialSetPackMultiplier as initialPackMultiplier from `" + packOptOutputTableName
				+ "` as SP where ProductFineline='" + planAndFineline + "' and "
				+ "SPInitialSetPackMultiplier>0 and SP.SPPackID='" + packId
				+ "') as SP on RFA.store = SP.store and RFA.cc = SP.cc "
				+ "group by RFA.finelineNbr, RFA.styleNbr,RFA.inStoreWeek,SP.packId ,SP.store,initialPackMultiplier "
				+ "order by RFA.finelineNbr, RFA.styleNbr,RFA.inStoreWeek,SP.packId ,SP.store,initialPackMultiplier) "		
				+ "UNION ALL" +
				" (select distinct RFA.finelineNbr, RFA.styleNbr,RFA.inStoreWeek, SP.packId, SP.store, (SP.initialPackMultiplier) AS initialPackMultiplier from "
						+ "(select fineline as finelineNbr, reverse( SUBSTR(REVERSE(trim(cc)), STRPOS(REVERSE(trim(cc)), \"_\")+1)) as styleNbr,trim(cc) as cc, CAST(store AS INTEGER) as store, min(week) as inStoreWeek "
						+ "from `" + parquetTableName + "` as RFA where plan_id_partition=" + planId + " and fineline="
						+ fineline + " and final_alloc_space>0"
						+ " group by finelineNbr, styleNbr,cc,store "
						+ "order by finelineNbr, styleNbr,cc, inStoreWeek, store ) as RFA " + "join "
						+ "(SELECT trim(SP.ProductCustomerChoice)as cc,SP.store, SP.SPPackID as packId, SP.MerchMethod as merch_method, SP.size, "
						+ "SP.SPBumpSetPackMultiplier as initialPackMultiplier from `" + packOptOutputTableName
						+ "` as SP where ProductFineline='" + planAndFineline + "' and "
						+ "SPBumpSetPackMultiplier>0 and SP.SPPackID='" + packId
						+ "') as SP on RFA.store = SP.store and RFA.cc = SP.cc "
						+ "group by RFA.finelineNbr, RFA.styleNbr,RFA.inStoreWeek,SP.packId ,SP.store,initialPackMultiplier "
						+ "order by RFA.finelineNbr, RFA.styleNbr,RFA.inStoreWeek,SP.packId ,SP.store,initialPackMultiplier)) "						
				+ "SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable";
	}
}
